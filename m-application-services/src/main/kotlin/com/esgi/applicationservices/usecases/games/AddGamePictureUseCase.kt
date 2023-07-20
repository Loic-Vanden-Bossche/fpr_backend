package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GamePictureUploader
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.exceptions.BadRequestException
import java.io.InputStream

class AddGamePictureUseCase(
    private val gamePictureUploader: GamePictureUploader,
    private val gamesPersistence: GamesPersistence,
) {
    private val contentTypes: List<String> = listOf(
        "image/png",
        "image/jpeg",
        "image/gif"
    )

    operator fun invoke(gameId: String, data: InputStream, contentType: String?): Game {
        if (contentType !in contentTypes) {
            throw BadRequestException("Content type $contentType is not allowed")
        }

        gamePictureUploader.upload(gameId, data, contentType)

        return gamesPersistence.setGamePicture(gameId)
    }
}