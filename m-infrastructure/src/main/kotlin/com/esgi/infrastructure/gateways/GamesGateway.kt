package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstanciator
import com.esgi.applicationservices.usecases.rooms.CreateRoomUseCase
import com.esgi.infrastructure.services.TcpService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.*
import java.nio.channels.AsynchronousSocketChannel

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    val gameInstanciator: GameInstanciator,
    val tcpService: TcpService,
    val simpMessagingTemplate: SimpMessagingTemplate,
    val createSessionUseCase: CreateRoomUseCase,
) {
    private val sessions: MutableMap<String, Session> = HashMap()

    @MessageMapping("/createRoom")
    @SendToUser("/rooms/created")
    fun createRoom(gameId: String): String? {
        println("Creating room with game $gameId")

        val room = createSessionUseCase()
        val roomId = room.id.toString()

        val session = Session(
            roomId,
            gameInstanciator.instanciateGame(gameId)
        )

        runBlocking {
            launch {
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
        }

        sessions[roomId] = session

        return "Created room $roomId"
    }

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun joinRoom(@DestinationVariable roomId: String): String? {
        println("Joining room $roomId")

        return "Joined room $roomId"
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String) {
        val session = sessions[roomId]

        if (session != null) {
            println("Starting game in room $roomId")
            session.sendInstruction("{\"init\": { \"players\": 2 }}\n")
        } else {
            println("Room $roomId not found")
        }
    }

    @MessageMapping("/play/{roomId}")
    fun play(@DestinationVariable roomId: String, instruction: String) {
        val room = sessions[roomId]

        if (room != null) {
            println("Sending instruction $instruction to room $roomId")
            room.sendInstruction(instruction)
        } else {
            println("Room $roomId not found")
        }
    }

    inner class Session(private val roomId: String,private val client: AsynchronousSocketChannel) {
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