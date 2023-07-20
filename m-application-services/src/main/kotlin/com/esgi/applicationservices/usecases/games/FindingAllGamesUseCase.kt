package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game

class FindingAllGamesUseCase(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(): List<Game> = gamesPersistence.findAll()
}