package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstantiator
import java.util.UUID

class GameInstantiatorDev(
    private val dockerService: DockerService,
    private val tcpService: TcpService
): GameInstantiator {
    override fun instanciateGame() {
        println("Game instantiated dev")
        val fakeGameId = UUID.randomUUID().toString()
        val containerId = dockerService.runContainer(fakeGameId, "fpr-executor-sample:latest")
        println("Container created")
        println("Container id: $containerId")
        val ip = dockerService.getContainerIpAddress(containerId)
        println("Container ip: $ip")

        println("Waiting for container to be ready")
        tcpService.init_test(ip)
    }
}