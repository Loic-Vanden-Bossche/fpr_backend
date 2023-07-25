package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstanciator
import com.esgi.applicationservices.usecases.rooms.*
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.SessionAction
import com.esgi.infrastructure.dto.input.GameErrorOutput
import com.esgi.infrastructure.dto.input.GameOutput
import com.esgi.infrastructure.dto.output.games.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.channels.AsynchronousSocketChannel
import java.util.*

@Service
class SessionsService(
    private val gameInstanciator: GameInstanciator,
    private val tcpService: TcpService,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val finalizeSessionUseCase: FinalizeSessionUseCase,
    private val pauseSessionUseCase: PauseSessionUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val findRoomUseCase: FindRoomUseCase,
    private val resumeSessionUseCase: ResumeSessionUseCase,
    private val pauseAllRoomsUseCase: PauseAllRoomsUseCase,
    private val getHistoryForRoomUseCase: GetHistoryForRoomUseCase,
    private val rollbackSessionUseCase: RollbackSessionUseCase,
) {
    private val sessions: MutableMap<String, Session> = HashMap()
    private val jsonMapper = jacksonObjectMapper()

    @EventListener(ApplicationReadyEvent::class)
    fun pauseAllRoomsOnStartup() {
        println("Startup - pausing all rooms that are not finished")
        pauseAllRoomsUseCase()
    }

    fun addSession(session: Session) {
        sessions[session.roomId] = session
    }

    fun createSessionAndListen(gameId: String, roomId: String): String {
        val message = createSession(gameId, roomId)

        listenToIncomingMessages(roomId)

        return message
    }

    fun createSession(gameId: String, roomId: String): String {
        val session = try {
            Session(
                roomId,
                gameInstanciator.instanciateGame(gameId),
            )
        } catch (e: Exception) {
            e.printStackTrace()

            deleteRoomUseCase(roomId)

            throw Exception("Error creating session")
        }

        addSession(session)

        return "Session created"
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun listenToIncomingMessages(roomId: String, actions: List<SessionAction> = emptyList()) {
        val session = getSession(roomId) ?: return

        GlobalScope.launch {
            val actionsSequence = LinkedList(actions)

            while (true) {
                try {
                    val jsonMessage: String? = session.receiveResponse()
                    val action = actionsSequence.poll()

                    if (action != null) {
                        println("Sending action ${action.instruction}")
                        session.sendInstruction(action.instruction)
                    }

                    if (jsonMessage != null) {
                        try {
                            session.dispatch(
                                jsonMapper.readValue<GameOutput>(jsonMessage),
                            )
                        } catch (e: IOException) {
                            try {
                                session.dispatchError(
                                    jsonMapper.readValue<GameErrorOutput>(jsonMessage),
                                )
                            } catch (e: IOException) {
                                println("Error parsing message")
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    session.stop(emptyList())
                    break
                }
            }
        }
    }

    fun rollbackSession(room: Room, actionId: String) {
        val roomId = room.id.toString()
        val actions = replayToAction(room, actionId)

        println("Rolling back session for room $roomId")

        createSession(room.game.id.toString(), roomId)

        listenToIncomingMessages(roomId, actions)
    }

    fun resumeSession(room: Room) {
        val roomId = room.id.toString()
        resumeSessionUseCase(roomId)

        println("Resuming session for room $roomId")

        createSession(room.game.id.toString(), roomId)

        listenToIncomingMessages(roomId, replayToAction(room, null))
    }

    private fun replayToAction(room: Room, actionId: String?): List<SessionAction> {
        val session = getSession(room.id.toString()) ?: return emptyList()

        session.startGame(room.players.size)

        return if (actionId == null) {
            getHistoryForRoomUseCase(room.id)
        } else {
            rollbackSessionUseCase(room.id, actionId)
        }
    }

    private fun getSession(roomId: String): Session? {
        return sessions[roomId]
    }

    fun sendLatestStateToUser(roomId: String, userId: UUID) {
        getSession(roomId)?.sendLatestStateToUser(userId.toString())
    }

    fun isSessionMissing(roomId: String): Boolean {
        return getSession(roomId) == null
    }

    fun isSessionExisting(roomId: String): Boolean {
        return getSession(roomId) != null
    }

    fun startGameSession(roomId: String, nPlayers: Int) {
        getSession(roomId)?.startGame(nPlayers)
    }

    fun stopGameSession(roomId: String, scores: List<Int>) {
        getSession(roomId)?.stop(scores)
    }

    fun pauseGameSession(roomId: String) {
        getSession(roomId)?.pause()
    }

    fun playGameAction(roomId: String, action: String) {
        getSession(roomId)?.sendInstruction(action)
    }

    inner class Session(
        val roomId: String,
        private val client: AsynchronousSocketChannel
    ) {
        private var lastStateMap: MutableMap<String, GameOutputResponseDto> = mutableMapOf()

        @OptIn(DelicateCoroutinesApi::class)
        val lastStateContext = newSingleThreadContext("LastStateContext")

        fun sendInstruction(message: String) {
            tcpService.sendTcpMessage(client, "$message\n\n")
        }

        fun receiveResponse(): String? {
            return tcpService.receiveTcpMessage(client)
        }

        fun sendLatestStateToUser(userId: String) {
            lastStateMap[userId]?.let {
                sentToUser(userId, it)
            }
        }

        fun startGame(nPlayers: Int) {
            sendInstruction(
                jsonMapper.writeValueAsString(
                    Instruction(Init(nPlayers))
                )
            )
        }

        private fun sentToUser(userId: String, message: Any) {
            simpMessagingTemplate.convertAndSend("/rooms/$roomId/${userId}", message)
        }

        fun dispatchError(gameErrorOutput: GameErrorOutput) {
            val room = findRoomUseCase(roomId) ?: return

            room.players.forEach {
                val gameErrorResponse = GameErrorOutputResponseDto(
                    errors = gameErrorOutput.errors.filter { error -> it.playerIndex == error.player }
                        .map { error ->
                            GameErrorResponseDto(
                                type = error.type,
                                subtype = error.subtype,
                                action = error.action,
                                requestedAction = error.requestedAction.let { action ->
                                    GameRequestedActionResponseDto(
                                        type = action.type,
                                        zones = action.zones
                                    )
                                }
                            )
                        }
                )

                if (gameErrorResponse.errors.isNotEmpty()) {
                    sentToUser(it.user.id.toString(), gameErrorResponse)
                }
            }
        }

        suspend fun dispatch(gameOutput: GameOutput) {
            val room = findRoomUseCase(roomId) ?: return

            room.players.forEach {
                val display = gameOutput.displays.find { display -> it.playerIndex == display.player }
                val requestActions = gameOutput.requestedActions.filter { action -> it.playerIndex == action.player }
                val gameState = gameOutput.gameState

                val gameResponse = GameOutputResponseDto(
                    gameState = GameStateResponseDto(
                        scores = gameState.scores.withIndex()
                            .associate { (index, score) -> room.players.find { p -> (p.playerIndex - 1) == index }?.user?.id.toString() to score },
                        gameOver = gameState.gameOver
                    ),
                    display = display?.let { d ->
                        GameDisplayResponseDto(
                            width = d.width,
                            height = d.height,
                            content = d.content
                        )
                    },
                    requestedActions = requestActions.map { action ->
                        GameRequestedActionResponseDto(
                            type = action.type,
                            zones = action.zones
                        )
                    }
                )

                withContext(lastStateContext) {
                    sessions[roomId]?.let { session ->
                        session.lastStateMap[it.user.id.toString()] = gameResponse
                    }
                }

                sentToUser(it.user.id.toString(), gameResponse)
            }

            if (gameOutput.gameState.gameOver) {
                stop(gameOutput.gameState.scores)
            }
        }

        fun stop(scores: List<Int>) {
            finalizeSessionUseCase(roomId, scores)
            close()
        }

        fun pause() {
            pauseSessionUseCase(roomId)
            close()
        }

        private fun close() {
            client.close()
            sessions.remove(roomId)
        }
    }

    data class Instruction(
        val init: Init
    )

    data class Init(
        val players: Int
    )
}