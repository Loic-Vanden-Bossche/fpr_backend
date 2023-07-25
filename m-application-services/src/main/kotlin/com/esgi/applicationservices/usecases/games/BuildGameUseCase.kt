package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.GameLanguage
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
    private val contentTypes: List<String> = listOf(
        "application/zip",
        "application/x-zip-compressed",
        "application/zip-compressed"
    )

    operator fun invoke(
        ownerId: UUID,
        gameId: String,
        fileStream: InputStream?,
        contentType: String?,
        language: String?
    ): Game {
        if (language == null) throw BadRequestException("Language is required to build game")
        if (fileStream == null) throw BadRequestException("File is required to build game")

        val game = gamesPersistence.findById(gameId) ?: throw NotFoundException("Game not found")

        if (game.owner.id != ownerId) {
            throw ForbiddenException("You are not the owner of this game")
        }

        GameLanguage.values().find { it.name == language.uppercase() }
            ?: throw BadRequestException("Language $language is not allowed")

        if (contentType !in contentTypes) {
            throw BadRequestException("Content type $contentType is not allowed")
        }

        gameUploader.uploadGame(gameId, fileStream)
        gameBuilder.buildGame(gameId, language.lowercase())

        return gamesPersistence.updateLastBuildDate(gameId)
    }
}