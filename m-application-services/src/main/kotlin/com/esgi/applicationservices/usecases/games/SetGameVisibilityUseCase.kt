package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.exceptions.ForbiddenException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class SetGameVisibilityUseCase(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(ownerId: UUID, gameId: String, visible: Boolean): Game {
        val game = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        if (game.owner.id != ownerId) {
            throw ForbiddenException("You are not the owner of this game")
        }

        return gamesPersistence.setGameVisibility(gameId, visible)
    }
}