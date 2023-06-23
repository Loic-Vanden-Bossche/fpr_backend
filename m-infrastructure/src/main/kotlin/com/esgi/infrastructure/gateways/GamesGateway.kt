package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.services.GameInstantiator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
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
) {
    private var currentRoom: String? = null
    private var gameStarted = false
    private var client: AsynchronousSocketChannel? = null

    @MessageMapping("/joinRoom")
    @SendToUser("/queue/reply")
    fun joinRoom(message: String?): String? {
        println("Joining room $message")
        currentRoom = message
        val room: String? = currentRoom
        gameStarted = false
        return room
    }

    @MessageMapping("/startGame")
    fun startGame(): String {
        try {
            gameInstantiator.instanciateGame("448a82c3-d29c-4921-8b45-480ae59a7cf1")

            Thread.sleep(5000)

            val address = InetSocketAddress(SERVER_IP, TCP_PORT)
            client = AsynchronousSocketChannel.open()

            client!!.connect(address)

            runBlocking {
                launch {
                    while (true) {
                        val jsonMessage: String = receiveTcpMessage()
                        println("Received: $jsonMessage")
                    }
                }

                sendTcpMessage("{\"init\": { \"players\": 2 }}\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "Game not started"
    }

    @MessageMapping("/play")
    @SendTo("/queue/reply")
    fun play(message: String): String {
        val playMessage: String = message
        sendTcpMessage("$playMessage\n")
        return playMessage
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

    companion object {
        private const val TCP_PORT = 8070
        private const val SERVER_IP = "127.0.0.1"
    }
}