package com.esgi.infrastructure.services
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.*
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient

import org.springframework.stereotype.Service
import java.net.URI

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

    fun runContainer(gameId: String, imageName: String): String {
        val exposedPort = ExposedPort.tcp(8070)
        val portBindings = Ports()
        portBindings.bind(exposedPort, Ports.Binding.bindPort(8070))

        val hostConfig = HostConfig.newHostConfig()
            .withPortBindings(portBindings)
            .withAutoRemove(true)

        val container = dockerClient.createContainerCmd(imageName)
            .withHostConfig(hostConfig)
            .withExposedPorts(exposedPort)
            .withName("game-$gameId")
            .exec()

        dockerClient.startContainerCmd(container.id).exec()

        return container.id
    }

    fun getContainerIpAddress(containerId: String): String {
        val containerInfo = dockerClient.inspectContainerCmd(containerId).exec()
        val networkSettings: NetworkSettings = containerInfo.networkSettings
        val networks = networkSettings.networks
        val networkName = networks.keys.firstOrNull() ?: ""
        return "127.0.0.1"
//        return networks[networkName]?.ipAddress ?: ""
    }
}