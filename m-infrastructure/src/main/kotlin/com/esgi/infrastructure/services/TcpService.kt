package com.esgi.infrastructure.services

import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

@Service
class TcpService {
    fun init_test(ip: String) {
        val address = InetSocketAddress("localhost", 8070)
        val client = AsynchronousSocketChannel.open()

        client.connect(address, null, object : CompletionHandler<Void?, Void?> {
            override fun completed(result: Void?, attachment: Void?) {
                // Connection successful, start sending/receiving data
                val message = "{\"init\": { \"players\": 2 }}\n"
                val writeBuffer = ByteBuffer.wrap(message.toByteArray())

                // Write data to the server
                client.write(writeBuffer, null, object : CompletionHandler<Int, Void?> {
                    override fun completed(bytesWritten: Int, attachment: Void?) {
                        if (writeBuffer.hasRemaining()) {
                            // Not all data has been written, continue writing
                            client.write(writeBuffer, null, this)
                        } else {
                            // All data has been written, start reading response
                            val readBuffer = ByteBuffer.allocate(2048)
                            client.read(readBuffer, null, object : CompletionHandler<Int, Void?> {
                                override fun completed(bytesRead: Int, attachment: Void?) {
                                    if (bytesRead == -1) {
                                        // Server closed the connection
                                        client.close()
                                        return
                                    }

                                    readBuffer.flip()
                                    val response = ByteArray(readBuffer.remaining())
                                    readBuffer.get(response)
                                    val responseData = String(response)
                                    println("Received: $responseData")

                                    // Continue reading if necessary
                                    readBuffer.clear()
                                    client.read(readBuffer, null, this)
                                }

                                override fun failed(exc: Throwable?, attachment: Void?) {
                                    // Handle read failure
                                    exc?.printStackTrace()
                                    client.close()
                                }
                            })
                        }
                    }

                    override fun failed(exc: Throwable?, attachment: Void?) {
                        // Handle write failure
                        exc?.printStackTrace()
                        client.close()
                    }
                })
            }

            override fun failed(exc: Throwable?, attachment: Void?) {
                // Handle connection failure
                exc?.printStackTrace()
                client.close()
            }
        })

        // Keep the main thread alive to allow asynchronous operations to complete
        Thread.currentThread().join()
    }
}