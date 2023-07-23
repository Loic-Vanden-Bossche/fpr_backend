package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.exceptions.NotFoundException

class FindingOneGameById(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(gameId: String) = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")
}