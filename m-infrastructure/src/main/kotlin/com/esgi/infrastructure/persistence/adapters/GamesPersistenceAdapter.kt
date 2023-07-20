package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import com.esgi.infrastructure.dto.mappers.GameMapper
import com.esgi.infrastructure.persistence.repositories.GamesRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class GamesPersistenceAdapter(
    private val gameRepository: GamesRepository,
): GamesPersistence {
    private val mapper = Mappers.getMapper(GameMapper::class.java)

    override fun findById(gameId: String): Game? {
        val gameEntity = gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: return null

        return mapper.toDomain(gameEntity)
    }
}