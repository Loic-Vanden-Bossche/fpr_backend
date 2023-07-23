package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GamePictureUploader
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.ForbiddenException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.io.InputStream
import java.util.UUID

class AddGamePictureUseCase(
    private val gamePictureUploader: GamePictureUploader,
    private val gamesPersistence: GamesPersistence,
) {
    private val contentTypes: List<String> = listOf(
        "image/png",
        "image/jpeg",
        "image/gif"
    )

    operator fun invoke(ownerId: UUID, gameId: String, data: InputStream, contentType: String?): Game {
        val game = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        if (game.owner.id != ownerId) {
            throw ForbiddenException("You are not the owner of this game")
        }

        if (contentType !in contentTypes) {
            throw BadRequestException("Content type $contentType is not allowed")
        }

        gamePictureUploader.upload(gameId, data, contentType)

        return gamesPersistence.setGamePicture(gameId)
    }
}