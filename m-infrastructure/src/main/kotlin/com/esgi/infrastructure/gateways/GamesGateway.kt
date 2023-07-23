package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstanciator
import com.esgi.applicationservices.usecases.rooms.CreateRoomUseCase
import com.esgi.applicationservices.usecases.rooms.JoinRoomUseCase
import com.esgi.applicationservices.usecases.rooms.PlaySessionActionUseCase
import com.esgi.applicationservices.usecases.rooms.StartSessionUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CreateRoomDto
import com.esgi.infrastructure.services.TcpService
import kotlinx.coroutines.GlobalScope
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

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    val gameInstanciator: GameInstanciator,
    val tcpService: TcpService,
    val simpMessagingTemplate: SimpMessagingTemplate,
    val createRoomUseCase: CreateRoomUseCase,
    val joinRoomUseCase: JoinRoomUseCase,
    val startSessionUseCase: StartSessionUseCase,
    val playSessionActionUseCase: PlaySessionActionUseCase
) {
    private val sessions: MutableMap<String, Session> = HashMap()

    @MessageMapping("/createRoom")
    @SendTo("/rooms/created")
    fun createRoom(principal: UsernamePasswordAuthenticationToken, roomData: CreateRoomDto): String? {
        println("Creating room with game ${roomData.gameId}")

        val room = createRoomUseCase(
            roomData.gameId,
            roomData.groupId,
            principal.principal as User,
        )
        val roomId = room.id.toString()

        val session = Session(
            roomId,
            gameInstanciator.instanciateGame(roomData.gameId)
        )

        GlobalScope.launch {
            while (true) {
                try {
                    val jsonMessage: String? = session.receiveResponse()

                    if (jsonMessage != null) {
                        session.broadcast(jsonMessage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        sessions[roomId] = session

        return "Created room $roomId"
    }

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun joinRoom(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String
    ): String? {
        println("Joining room $roomId")

        joinRoomUseCase(roomId, (principal.principal as User).id.toString())

        return "Joined room $roomId"
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String) {
        val session = sessions[roomId]

        if (session != null) {
            println("Starting game in room $roomId")

            val room = startSessionUseCase(roomId)

            session.sendInstruction("{\"init\": { \"players\": ${room.players.size} }}\n")
        } else {
            println("Room game client $roomId not found")
        }
    }

    @MessageMapping("/play/{roomId}")
    fun play(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String,
        instruction: String
    ) {
        val session = sessions[roomId]

        if (session != null) {
            playSessionActionUseCase(roomId, (principal.principal as User).id.toString(), instruction)

            println("Sending instruction $instruction to room $roomId")
            session.sendInstruction(instruction)
        } else {
            println("Room game client $roomId not found")
        }
    }

    inner class Session(private val roomId: String, private val client: AsynchronousSocketChannel) {
        fun sendInstruction(message: String) {
            tcpService.sendTcpMessage(client, "$message\n")
        }

        fun receiveResponse(): String? {
            return tcpService.receiveTcpMessage(client)
        }

        fun broadcast(message: String) {
            simpMessagingTemplate.convertAndSend("/rooms/$roomId", message)
        }
    }
}