package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.repositories.GamesRepository
import com.esgi.infrastructure.persistence.repositories.RoomsRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class RoomsPersistenceAdapter(
    private val roomsRepository: RoomsRepository,
    private val usersRepository: UsersRepository,
    private val gamesRepository: GamesRepository,
): RoomsPersistence {
    private val mapper = Mappers.getMapper(RoomMapper::class.java)

    override fun findById(roomId: String): Room? {
        val roomEntity = roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: return null

        return mapper.toDomain(roomEntity)
    }

    override fun create(gameId: String, owner: User): Room {
        val userEntity = usersRepository.findById(owner.id).orElse(null) ?: throw Exception("User not found")
        val gameEntity = gamesRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw Exception("Game not found")

        val roomEntity = RoomEntity(
            owner = userEntity,
            status = RoomStatus.WAITING,
            players = listOf(userEntity),
            game = gameEntity
        )

        return mapper.toDomain(roomsRepository.save(roomEntity))
    }
}