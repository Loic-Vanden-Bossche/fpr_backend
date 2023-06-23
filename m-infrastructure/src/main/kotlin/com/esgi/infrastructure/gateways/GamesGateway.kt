package com.esgi.infrastructure.gateways

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.StandardCharsets


@Controller
@CrossOrigin(origins = ["http://localhost:1234"], allowCredentials = "true")
class GamesGateway {
    private var currentRoom: String? = null
    private var gameStarted = false
    private var gameSocket: Socket? = null
    private var gameInputStream: InputStream? = null
    private var gameOutputStream: OutputStream? = null

    @MessageMapping("/joinRoom")
    @SendToUser("/queue/reply")
    fun joinRoom(message: String?): String? {
        // Create or join the room based on the message and return the room object
        // Logic to create/join room
        currentRoom = message
        val room: String? = currentRoom
        gameStarted = false
        return room
    }

    @MessageMapping("/startGame")
    @SendTo("/queue/reply")
    fun startGame(): String {
        if (currentRoom != null && !gameStarted) {
            try {
                gameSocket = Socket(SERVER_IP, TCP_PORT)
                gameInputStream = gameSocket!!.getInputStream()
                gameOutputStream = gameSocket!!.getOutputStream()

                // Start the game and send an init message
                gameStarted = true
                val initMessage = "{\"init\": { \"players\": 2 }}"
                sendTcpMessage(initMessage)
                return initMessage
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return "Game not started"
    }

    @MessageMapping("/play")
    @SendTo("/queue/reply")
    fun play(message: String): String {
        if (currentRoom != null && gameStarted) {
            // Process the play message and send it to the game server
            val playMessage: String = message
            sendTcpMessage(playMessage)
            return playMessage
        }
        return "Invalid play"
    }

    private fun sendTcpMessage(message: String) {
        try {
            val data: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
            gameOutputStream!!.write(data)
            gameOutputStream!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TCP_PORT = 8070
        private const val SERVER_IP = "127.0.0.1"
    }
}