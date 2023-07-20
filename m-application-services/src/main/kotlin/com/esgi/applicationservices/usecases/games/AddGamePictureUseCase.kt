package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GamePictureUploader
import com.esgi.domainmodels.Game
import java.io.InputStream

class AddGamePictureUseCase(
    private val gamePictureUploader: GamePictureUploader,
    private val gamesPersistence: GamesPersistence,
) {
    operator fun invoke(gameId: String, data: InputStream, contentType: String?): Game {
        gamePictureUploader.upload(gameId, data, contentType)

        return gamesPersistence.setGamePicture(gameId)
    }
}