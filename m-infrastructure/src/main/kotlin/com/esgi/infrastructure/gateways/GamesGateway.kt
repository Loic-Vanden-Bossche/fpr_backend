package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstantiator
import com.esgi.infrastructure.services.TcpService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.*
import java.nio.channels.AsynchronousSocketChannel

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    val gameInstantiator: GameInstantiator,
    val tcpService: TcpService,
    val simpMessagingTemplate: SimpMessagingTemplate
) {
    private val rooms: MutableMap<String, Room> = HashMap()

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun joinRoom(@DestinationVariable roomId: String, message: String): String? {
        println("Joining room $roomId")
        val room = rooms.computeIfAbsent(roomId) { Room(roomId) }
        room.addPlayer(message)
        return "Joined room $roomId"
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String) {
        try {
            val room = rooms[roomId]
            if (room != null) {
                room.client = gameInstantiator.instanciateGame("448a82c3-d29c-4921-8b45-480ae59a7cf1")

                runBlocking {
                    launch {
//                        while (true) {
//                            val jsonMessage: String? = room.receiveResponse()
//
//                            if (jsonMessage != null) {
//                                room.broadcast(jsonMessage)
//                            } else {
//                                println("Received null or empty message")
//                            }
//                        }
                    }

                    room.sendInstruction("{\"init\": { \"players\": 2 }}\n")
                }
            } else {
                println("Room $roomId not found")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @MessageMapping("/play/{roomId}")
    fun play(@DestinationVariable roomId: String, instruction: String) {
        val room = rooms[roomId]

        if (room != null) {
            println("Sending instruction $instruction to room $roomId")
            room.sendInstruction(instruction)
        } else {
            println("Room $roomId not found")
        }
    }

    inner class Room(private val roomId: String) {
        private val players: MutableSet<String> = HashSet()
        var client: AsynchronousSocketChannel? = null

        fun addPlayer(playerId: String) {
            players.add(playerId)
        }

        fun sendInstruction(message: String) {
            tcpService.sendTcpMessage(client!!, "$message\n")
        }

        fun receiveResponse(): String? {
            return tcpService.receiveTcpMessage(client!!)
        }

        fun broadcast(message: String) {
            simpMessagingTemplate.convertAndSend("/rooms/$roomId", message)
        }
    }
}