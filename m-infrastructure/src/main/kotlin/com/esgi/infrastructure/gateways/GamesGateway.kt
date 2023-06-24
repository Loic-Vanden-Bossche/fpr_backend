package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstantiator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    val gameInstantiator: GameInstantiator,
    val simpMessagingTemplate: SimpMessagingTemplate
) {
    private var client: AsynchronousSocketChannel? = null
    private val rooms: MutableMap<String, Room> = HashMap()

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun joinRoom(@DestinationVariable roomId: String, message: String): String? {
        println("Joining room $roomId")
        val room = rooms.computeIfAbsent(roomId) { Room(roomId) }
        room.addPlayer(message)
        return "Joined room $roomId"
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/topic/room/{roomId}")
    fun startGame(@DestinationVariable roomId: String) {
        try {
            val room = rooms[roomId]
            if (room != null) {
                gameInstantiator.instanciateGame("448a82c3-d29c-4921-8b45-480ae59a7cf1")

                val address = InetSocketAddress(SERVER_IP, TCP_PORT)
                client = AsynchronousSocketChannel.open()

                client!!.connect(address)

                runBlocking {
                    launch {
                        while (true) {
                            val jsonMessage: String = receiveTcpMessage()
                            println("Received: $jsonMessage")
                            room.broadcast(jsonMessage) // Broadcast game messages to all players in the room
                        }
                    }

                    sendTcpMessage("{\"init\": { \"players\": 2 }}\n")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @MessageMapping("/play/{roomId}")
    fun play(message: String) {
        val room = getRoomByPlayerId(message)
        if (room != null) {
            sendTcpMessage("$message\n")
        }
    }

    private fun receiveTcpMessage(): String {
        try {
            val readBuffer = ByteBuffer.allocate(2048)

            client!!.read(readBuffer).get()

            readBuffer.flip()

            val response = ByteArray(readBuffer.remaining())

            readBuffer.get(response)

            return String(response)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "Invalid message"
    }

    private fun sendTcpMessage(message: String) {
        try {
            val writeBuffer = ByteBuffer.wrap(message.toByteArray())
            client!!.write(writeBuffer).get()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getRoomByPlayerId(playerId: String): Room? {
        for (room in rooms.values) {
            if (room.hasPlayer(playerId)) {
                return room
            }
        }
        return null
    }

    inner class Room(private val roomId: String) {
        private val players: MutableSet<String> = HashSet()

        fun addPlayer(playerId: String) {
            players.add(playerId)
        }

        fun hasPlayer(playerId: String): Boolean {
            return players.contains(playerId)
        }

        fun broadcast(message: String) {
            simpMessagingTemplate.convertAndSend("/topic/room/$roomId", message)
        }
    }

    companion object {
        private const val TCP_PORT = 8070
        private const val SERVER_IP = "127.0.0.1"
    }
}