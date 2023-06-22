package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import java.io.InputStream

class BuildGameUseCase(
    private val gameUploader: GameUploader,
    private val gameBuilder: GameBuilder,
) {
    fun execute(gameId: String, fileStream: InputStream) {
        gameUploader.uploadGame(gameId, fileStream)
        gameBuilder.buildGame(gameId)
    }
}