package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.GameMapper
import com.esgi.infrastructure.persistence.entities.GameEntity
import com.esgi.infrastructure.persistence.repositories.GamesRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class GamesPersistenceAdapter(
    private val gameRepository: GamesRepository,
    private val usersRepository: UsersRepository
): GamesPersistence {
    private val mapper = Mappers.getMapper(GameMapper::class.java)

    override fun findAll(): List<Game> {
        return gameRepository.findAll().map { gameEntity ->
            mapper.toDomain(gameEntity)
        }
    }

    override fun findById(gameId: String): Game? {
        val gameEntity = gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: return null

        return mapper.toDomain(gameEntity)
    }

    override fun createGame(
        title: String,
        nbMinPlayers: Int,
        nbMaxPlayers: Int,
        isDeterministic: Boolean,
        owner: User
    ): Game {
        val userEntity = usersRepository.findById(owner.id).orElse(null) ?: throw Exception("User not found")

        val gameEntity = gameRepository.save(
            GameEntity(
                title = title,
                nbMinPlayers = nbMinPlayers,
                nbMaxPlayers = nbMaxPlayers,
                isDeterministic = isDeterministic,
                owner = userEntity,
                rooms = emptyList(),
            )
        )

        return mapper.toDomain(gameEntity)
    }
}