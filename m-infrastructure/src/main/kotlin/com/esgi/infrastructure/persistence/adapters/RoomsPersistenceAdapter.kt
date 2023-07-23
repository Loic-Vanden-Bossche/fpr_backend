package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomInvitationStatus
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.entities.SessionActionEntity
import com.esgi.infrastructure.persistence.repositories.*
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class RoomsPersistenceAdapter(
    private val roomsRepository: RoomsRepository,
    private val usersRepository: UsersRepository,
    private val gamesRepository: GamesRepository,
    private val groupsRepository: GroupsRepository,
    private val sessionActionRepository: SessionActionRepository
) : RoomsPersistence {
    private val mapper = Mappers.getMapper(RoomMapper::class.java)

    override fun findById(roomId: String): Room? {
        val roomEntity = roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: return null

        return mapper.toDomain(roomEntity, null)
    }

    override fun create(gameId: String, groupId: String, owner: User): Room {
        val userEntity = usersRepository.findById(owner.id).orElse(null) ?: throw NotFoundException("User not found")
        val gameEntity =
            gamesRepository.findById(UUID.fromString(gameId)).orElse(null) ?: throw NotFoundException("Game not found")
        val groupEntity = groupsRepository.findById(UUID.fromString(groupId)).orElse(null)
            ?: throw NotFoundException("Group not found")

        val roomEntity = RoomEntity(
            owner = userEntity,
            status = RoomStatus.WAITING,
            players = listOf(userEntity),
            game = gameEntity,
            group = groupEntity,
            actions = listOf()
        )

        return mapper.toDomain(roomsRepository.save(roomEntity), null)
    }

    override fun addPlayer(roomId: String, userId: String) {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")
        val userEntity =
            usersRepository.findById(UUID.fromString(userId)).orElse(null) ?: throw NotFoundException("User not found")

        roomEntity.players += userEntity

        roomsRepository.save(roomEntity)
    }

    override fun removePlayer(roomId: String, userId: String) {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")
        val userEntity =
            usersRepository.findById(UUID.fromString(userId)).orElse(null) ?: throw NotFoundException("User not found")

        roomEntity.players -= userEntity

        roomsRepository.save(roomEntity)
    }

    override fun updateStatus(roomId: String, status: RoomStatus): Room {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")

        roomEntity.status = status

        return mapper.toDomain(roomsRepository.save(roomEntity), null)
    }

    override fun recordAction(roomId: String, userId: String, instruction: String) {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")
        val userEntity =
            usersRepository.findById(UUID.fromString(userId)).orElse(null) ?: throw NotFoundException("User not found")

        val sessionAction = SessionActionEntity(
            instruction = instruction,
            player = userEntity
        )

        sessionActionRepository.save(sessionAction)

        roomEntity.actions += sessionAction

        roomsRepository.save(roomEntity)
    }

    override fun delete(roomId: String) {
        roomsRepository.deleteById(UUID.fromString(roomId))
    }

    override fun getUserRooms(userId: UUID): List<Room> {
        return roomsRepository.findAllByPlayersContains(userId).map {
            mapper.toDomain(
                it.getRoomEntity(),
                RoomInvitationStatus.values().find { status -> status.name == it.getStatus() })
        }
    }
}