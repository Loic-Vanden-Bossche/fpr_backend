package com.esgi.infrastructure.services

import org.springframework.stereotype.Service
import java.io.EOFException
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel

@Service
class TcpService {
    fun createClient(ipAddress: String): AsynchronousSocketChannel {
        val address = InetSocketAddress(ipAddress, 8070)
        val client = AsynchronousSocketChannel.open()

        client!!.connect(address).get()

        return client
    }

    fun sendTcpMessage(client: AsynchronousSocketChannel, message: String) {
        try {
            val writeBuffer = ByteBuffer.wrap(message.toByteArray())
            client.write(writeBuffer).get()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun receiveTcpMessage(client: AsynchronousSocketChannel): String? {
        try {
            val readBuffer = ByteBuffer.allocate(2048)

            if(client.read(readBuffer).get() == -1){
                throw EOFException()
            }

            readBuffer.flip()

            val response = ByteArray(readBuffer.remaining())

            readBuffer.get(response)

            return String(response)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}