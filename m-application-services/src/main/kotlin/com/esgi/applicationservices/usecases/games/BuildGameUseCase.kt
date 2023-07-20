package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.io.InputStream

class BuildGameUseCase(
    private val gameUploader: GameUploader,
    private val gameBuilder: GameBuilder,
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(gameId: String, fileStream: InputStream, contentType: String?): Game {
        if (contentType != "application/zip") throw BadRequestException("Content type $contentType is not allowed")

        gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        gameUploader.uploadGame(gameId, fileStream)
        gameBuilder.buildGame(gameId)

        return gamesPersistence.updateLastBuildDate(gameId)
    }
}