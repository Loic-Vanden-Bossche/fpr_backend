package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstantiator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*

@Profile("dev")
@Service
class GameInstantiatorDev(
    private val dockerService: DockerService,
    private val tcpService: TcpService
): GameInstantiator {
    override fun instanciateGame(gameId: String) {
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

        tcpService.init_test("localhost")
    }
}