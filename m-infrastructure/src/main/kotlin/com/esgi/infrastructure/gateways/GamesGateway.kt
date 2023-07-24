package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstanciator
import com.esgi.applicationservices.usecases.rooms.*
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.input.CreateRoomDto
import com.esgi.infrastructure.dto.input.GameErrorOutput
import com.esgi.infrastructure.dto.input.GameOutput
import com.esgi.infrastructure.dto.output.games.*
import com.esgi.infrastructure.services.TcpService
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.IOException
import java.nio.channels.AsynchronousSocketChannel

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    val gameInstanciator: GameInstanciator,
    val tcpService: TcpService,
    val simpMessagingTemplate: SimpMessagingTemplate,
    val createRoomUseCase: CreateRoomUseCase,
    val joinRoomUseCase: JoinRoomUseCase,
    val startSessionUseCase: StartSessionUseCase,
    val playSessionActionUseCase: PlaySessionActionUseCase,
    val deleteRoomUseCase: DeleteRoomUseCase,
    val findRoomUseCase: FindRoomUseCase,
    val finalizeSessionUseCase: FinalizeSessionUseCase,
    val pauseSessionUseCase: PauseSessionUseCase
) {
    private val sessions: MutableMap<String, Session> = HashMap()
    val jsonMapper = jacksonObjectMapper()

    @MessageMapping("/createRoom")
    @SendTo("/rooms/created")
    fun createRoom(principal: UsernamePasswordAuthenticationToken, roomData: CreateRoomDto): CreateRoomResponseDto {
        println("Creating room with game ${roomData.gameId}")

        val room = try {
            createRoomUseCase(
                roomData.gameId,
                roomData.groupId,
                principal.principal as User,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return CreateRoomResponseDto(false, reason = "Error creating room")
        }

        val roomId = room.id

        val session = try {
            Session(
                roomId.toString(),
                gameInstanciator.instanciateGame(roomData.gameId),
            )
        } catch (e: Exception) {
            e.printStackTrace()

            deleteRoomUseCase(roomId.toString())

            return CreateRoomResponseDto(false, reason = "Error creating session")
        }

        GlobalScope.launch {
            while (true) {
                try {
                    val jsonMessage: String? = session.receiveResponse()

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

        sessions[roomId.toString()] = session

        return CreateRoomResponseDto(true, roomId)
    }

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun joinRoom(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String
    ): JoinRoomResponseDto {
        println("Joining room $roomId")

        val session = sessions[roomId]
        val userId = (principal.principal as User).id

        return try {
            joinRoomUseCase(roomId, userId.toString())
            JoinRoomResponseDto(true, userId)
        } catch (e: IllegalStateException) {
            session?.sendLatestStateToUser(userId.toString())

            JoinRoomResponseDto(true)
        } catch (e: BadRequestException) {
            JoinRoomResponseDto(false, reason = e.message)
        } catch (e: NotFoundException) {
            JoinRoomResponseDto(false, reason = e.message)
        }
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String): StartedGameResponseDto {
        val session = sessions[roomId] ?: return StartedGameResponseDto(false, "Session not found")

        return try {
            val room = startSessionUseCase(roomId)
            session.sendInstruction(jsonMapper.writeValueAsString(
                Instruction(Init(room.players.size)))
            )
            StartedGameResponseDto(true)
        } catch (e: IllegalStateException) {
            StartedGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            StartedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/stopGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun stopGame(@DestinationVariable roomId: String): StoppedGameResponseDto {
        val session = sessions[roomId] ?: return StoppedGameResponseDto(false, "Session not found")

        return try {
            session.stop(emptyList())
            StoppedGameResponseDto(true)
        } catch (e: NotFoundException) {
            StoppedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/pauseGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun pauseGame(@DestinationVariable roomId: String): PausedGameResponseDto {
        val session = sessions[roomId] ?: return PausedGameResponseDto(false, "Session not found")

        return try {
            session.pause()
            PausedGameResponseDto(true)
        } catch (e: NotFoundException) {
            PausedGameResponseDto(false, e.message)
        } catch (e: BadRequestException) {
            PausedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/play/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun play(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String,
        instruction: String
    ): PlayGameResponseDto {
        val session = sessions[roomId]
        val room = findRoomUseCase(roomId)

        val player = room?.players?.find { it.user.id == (principal.principal as User).id }
            ?: return PlayGameResponseDto(false, "You are not in this room")

        val parsedInstruction = try {
            val tree = jsonMapper.readTree(instruction)

            if (tree["actions"].isArray) {
                tree["actions"].forEach {
                    if (it.isObject) {
                        (it as ObjectNode).put("player", player.playerIndex)
                    }
                }
            }

            tree
        } catch (_: Exception) {
            PlayGameResponseDto(false, "Invalid JSON instruction")
        }

        if (session != null) {
            return try {
                playSessionActionUseCase(
                    roomId,
                    (principal.principal as User).id.toString(),
                    jsonMapper.writeValueAsString(parsedInstruction)
                )

                println("Sending instruction $parsedInstruction to room $roomId")
                session.sendInstruction(parsedInstruction.toString())

                PlayGameResponseDto(true)
            } catch (e: BadRequestException) {
                PlayGameResponseDto(false, e.message)
            } catch (e: NotFoundException) {
                PlayGameResponseDto(false, e.message)
            }
        }

        return PlayGameResponseDto(false, "Session not found")
    }

    inner class Session(
        private val roomId: String,
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
                        scores = gameState.scores,
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