package com.esgi.infrastructure.services

import com.amazonaws.services.ecs.model.*
import com.esgi.applicationservices.services.GameBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("dev")
@Service
class BuildGameServiceDev: GameBuilder {
    @Value("\${games-bucket.name}")
    private val bucketName: String? = null

    override fun buildGame(gameId: String) {
        println(bucketName)
        println(gameId)
    }
}