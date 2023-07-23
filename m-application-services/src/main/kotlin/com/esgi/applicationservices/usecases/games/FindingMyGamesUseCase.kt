package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import java.util.UUID

class FindingMyGamesUseCase(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(userId: UUID): List<Game> = gamesPersistence.findAllByUserId(userId.toString())
}