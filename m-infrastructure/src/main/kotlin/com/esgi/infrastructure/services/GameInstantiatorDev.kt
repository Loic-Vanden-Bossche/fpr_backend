package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstanciator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.nio.channels.AsynchronousSocketChannel

@Profile("dev")
@Service
class GameInstantiatorDev(
    private val dockerService: DockerService,
    private val tcpService: TcpService,
): GameInstanciator {
    @Value("\${aws.region}")
    private val awsRegion: String? = null

    @Value("\${aws.accountId}")
    private val awsAccountId: String? = null

    override fun instanciateGame(gameId: String): AsynchronousSocketChannel {
        println("Game instantiated dev")

        val containerId = dockerService.runGameContainer(
            gameId,
            "$awsAccountId.dkr.ecr.${awsRegion}.amazonaws.com/fpr-games-repository:${gameId}",
        )

        println("Container created")
        println("Container id: $containerId")

        println("Waiting for container to be ready")

        Thread.sleep(5000)

        return tcpService.createClient("localhost")
    }
}