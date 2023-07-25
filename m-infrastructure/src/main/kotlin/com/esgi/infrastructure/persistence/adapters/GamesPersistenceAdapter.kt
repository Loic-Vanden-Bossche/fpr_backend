package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.mappers.GameMapper
import com.esgi.infrastructure.persistence.entities.GameEntity
import com.esgi.infrastructure.persistence.repositories.GamesRepository
import com.esgi.infrastructure.persistence.repositories.RoomsRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class GamesPersistenceAdapter(
    private val gameRepository: GamesRepository,
    private val usersRepository: UsersRepository,
    private val roomsRepository: RoomsRepository
) : GamesPersistence {
    private val mapper = Mappers.getMapper(GameMapper::class.java)

    override fun findAll(): List<Game> {
        return gameRepository.findAllByLastBuildDateIsNotNull().map { gameEntity ->
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
        val userEntity = usersRepository.findById(owner.id).orElse(null) ?: throw NotFoundException("User not found")

        val gameEntity = gameRepository.save(
            GameEntity(
                title = title,
                nbMinPlayers = nbMinPlayers,
                nbMaxPlayers = nbMaxPlayers,
                isDeterministic = isDeterministic,
                owner = userEntity,
                isPublic = false,
                lastBuildDate = null,
                rooms = emptyList(),
                picture = false,
                needSeed = false
            )
        )

        return mapper.toDomain(gameEntity)
    }

    override fun updateLastBuildDate(gameId: String): Game {
        val gameEntity =
            gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")

        gameEntity.lastBuildDate = Date()

        return mapper.toDomain(gameRepository.save(gameEntity))
    }

    override fun setGamePicture(gameId: String): Game {
        val gameEntity =
            gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")

        gameEntity.picture = true

        return mapper.toDomain(gameRepository.save(gameEntity))
    }

    override fun setGameVisibility(gameId: String, visible: Boolean): Game {
        val gameEntity =
            gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")

        gameEntity.isPublic = visible

        return mapper.toDomain(gameRepository.save(gameEntity))
    }

    override fun setNeedSeed(gameId: String, needSeed: Boolean): Game {
        val gameEntity =
            gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")

        gameEntity.needSeed = needSeed

        return mapper.toDomain(gameRepository.save(gameEntity))
    }

    override fun delete(gameId: String) {
        val gameEntity =
            gameRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")

        roomsRepository.setGameToNull(UUID.fromString(gameId))
        gameRepository.delete(gameEntity)
    }

    override fun findAllByUserId(userId: String): List<Game> {
        val userEntity =
            usersRepository.findById(UUID.fromString(userId)).orElse(null) ?: throw NotFoundException("User not found")

        return gameRepository.findAllByOwner(userEntity).map { gameEntity ->
            mapper.toDomain(gameEntity)
        }
    }
}