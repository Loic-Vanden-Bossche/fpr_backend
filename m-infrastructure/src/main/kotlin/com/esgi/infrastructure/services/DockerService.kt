package com.esgi.infrastructure.services
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.*
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import org.springframework.context.annotation.Profile

import org.springframework.stereotype.Service
import java.net.URI

@Profile("dev")
@Service
class DockerService {
    private val dockerClient: DockerClient

    init {
        try {
            val dockerHost = System.getenv("DOCKER_HOST") ?: "tcp://127.0.0.1:2375"
            val dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build()
            val httpclient = ApacheDockerHttpClient.Builder().dockerHost(URI(dockerHost)).build()
            dockerClient = DockerClientBuilder
                .getInstance(dockerConfig)
                .withDockerHttpClient(httpclient)
                .build()
        } catch (e: Exception) {
            throw Exception("Failed to initialize Docker client", e)
        }
    }

    fun runGameContainer(gameId: String, imageName: String): String {
        val exposedPort = ExposedPort.tcp(8070)
        val portBindings = Ports()
        portBindings.bind(exposedPort, Ports.Binding.bindPort(8070))

        val hostConfig = HostConfig.newHostConfig()
            .withPortBindings(portBindings)
//            .withAutoRemove(true)

        val container = dockerClient.createContainerCmd(imageName)
            .withHostConfig(hostConfig)
            .withExposedPorts(exposedPort)
            .withName("game-$gameId")
            .exec()

        dockerClient.startContainerCmd(container.id).exec()

        return container.id
    }
}