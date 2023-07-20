package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.domainmodels.Game
import java.io.InputStream

class BuildGameUseCase(
    private val gameUploader: GameUploader,
    private val gameBuilder: GameBuilder,
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(gameId: String, fileStream: InputStream): Game {
        gamesPersistence.findById(gameId) ?: throw Exception("Game not found")

        gameUploader.uploadGame(gameId, fileStream)
        gameBuilder.buildGame(gameId)

        return gamesPersistence.updateLastBuildDate(gameId)
    }
}