package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstanciator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.nio.channels.AsynchronousSocketChannel

@Profile("dev")
@Service
class GameInstantiatorDev(
    private val dockerService: DockerService,
    private val tcpService: TcpService,
): GameInstanciator {
    override fun instanciateGame(gameId: String): AsynchronousSocketChannel {
        println("Game instantiated dev")

        val registryId = "075626265631"

        val containerId = dockerService.runGameContainer(
            gameId,
            "$registryId.dkr.ecr.eu-west-3.amazonaws.com/fpr-games-repository:${gameId}",
        )

        println("Container created")
        println("Container id: $containerId")

        println("Waiting for container to be ready")

        Thread.sleep(5000)

        return tcpService.createClient("localhost")
    }
}