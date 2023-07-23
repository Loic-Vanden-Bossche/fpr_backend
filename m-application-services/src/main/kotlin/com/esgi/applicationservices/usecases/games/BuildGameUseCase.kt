package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.ForbiddenException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.io.InputStream
import java.util.*

class BuildGameUseCase(
    private val gameUploader: GameUploader,
    private val gameBuilder: GameBuilder,
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(ownerId: UUID, gameId: String, fileStream: InputStream, contentType: String?): Game {
        val game = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        if (game.owner.id != ownerId) {
            throw ForbiddenException("You are not the owner of this game")
        }

        if (contentType != "application/zip") throw BadRequestException("Content type $contentType is not allowed")

        gameUploader.uploadGame(gameId, fileStream)
        gameBuilder.buildGame(gameId)

        return gamesPersistence.updateLastBuildDate(gameId)
    }
}