package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.exceptions.ForbiddenException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class DeleteGameUseCase(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(ownerId: UUID, gameId: String) {
        val game = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        println(game.owner.id)
        println(ownerId)

        if (game.owner.id != ownerId) {
            throw ForbiddenException("You are not the owner of this game")
        }

        gamesPersistence.delete(gameId)
    }
}