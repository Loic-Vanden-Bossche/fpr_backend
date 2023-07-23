package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstanciator
import com.esgi.applicationservices.usecases.rooms.*
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.input.CreateRoomDto
import com.esgi.infrastructure.dto.input.GameOutput
import com.esgi.infrastructure.dto.output.games.CreateRoomResponseDto
import com.esgi.infrastructure.dto.output.games.JoinRoomResponseDto
import com.esgi.infrastructure.dto.output.games.StartedGameResponseDto
import com.esgi.infrastructure.services.TcpService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.IOException
import java.nio.channels.AsynchronousSocketChannel
import kotlin.coroutines.coroutineContext

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
                gameInstanciator.instanciateGame(roomData.gameId)
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
                        val gameOutput = jsonMapper.readValue<GameOutput>(jsonMessage)

                        session.broadcast(gameOutput)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
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

        return try {
            joinRoomUseCase(roomId, (principal.principal as User).id.toString())
            JoinRoomResponseDto(true)
        } catch (e: BadRequestException){
            JoinRoomResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            JoinRoomResponseDto(false, e.message)
        }
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String): StartedGameResponseDto {
        val session = sessions[roomId] ?: return StartedGameResponseDto(false, "Session not found")

        return try {
            val room = startSessionUseCase(roomId)
            session.sendInstruction(jsonMapper.writeValueAsString(Instruction(Init(room.players.size))))
            StartedGameResponseDto(true)
        } catch (e: IllegalStateException) {
            StartedGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            StartedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/play/{roomId}")
    fun play(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String,
        instruction: String
    ) {
        val session = sessions[roomId]

        try {
            jsonMapper.readTree(instruction)
        }catch (_: Exception){
            println("Not json")
        }

        if (session != null) {
            playSessionActionUseCase(roomId, (principal.principal as User).id.toString(), jsonMapper.writeValueAsString(instruction))

            println("Sending instruction $instruction to room $roomId")
            session.sendInstruction(instruction)
        } else {
            println("Room game client $roomId not found")
        }
    }

    inner class Session(private val roomId: String, private val client: AsynchronousSocketChannel) {
        fun sendInstruction(message: String) {
            tcpService.sendTcpMessage(client, "$message}\n\n")
        }

        fun receiveResponse(): String? {
            return tcpService.receiveTcpMessage(client)
        }

        fun broadcast(gameOutput: GameOutput) {
            simpMessagingTemplate.convertAndSend("/rooms/$roomId", gameOutput)
        }
    }

    data class Instruction(
        val init: Init
    )

    data class Init (
        val players: Int
    )
}