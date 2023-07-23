package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.*
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.persistence.entities.PlayerEntity
import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.entities.SessionActionEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
import com.esgi.infrastructure.persistence.repositories.*
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
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

    override fun findByIdOfUser(roomId: UUID, user: UUID): Room? {
        val roomEntity = roomsRepository.findByIdOrNull(roomId) ?: return null
        val status = if(user in roomEntity.players.map { it.user.id }){
            RoomInvitationStatus.JOINED
        } else if(user in roomEntity.group.users.map { it.user.id }){
            RoomInvitationStatus.JOINED
        } else {
            return null
        }
        return mapper.toDomain(roomEntity, status)
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
            players = mutableListOf(),
            game = gameEntity,
            group = groupEntity,
            actions = listOf()
        )

        val playerEntity = PlayerEntity(
            user = userEntity,
            room = roomEntity,
            playerIndex = null
        )

        roomEntity.players += playerEntity

        return mapper.toDomain(roomsRepository.save(roomEntity), null)
    }

    override fun addPlayer(roomId: String, userId: String) {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")
        val userEntity =
            usersRepository.findById(UUID.fromString(userId)).orElse(null) ?: throw NotFoundException("User not found")

        val playerEntity = PlayerEntity(
            user = userEntity,
            room = roomEntity,
            playerIndex = null
        )

        roomEntity.players += playerEntity

        roomsRepository.save(roomEntity)
    }

    override fun calculatePlayerIndexes(roomId: String): Room {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")

        val players = roomEntity.players.shuffled()

        players.forEachIndexed { index, player ->
            player.playerIndex = index
        }

        roomEntity.players = players.toMutableList()

        return mapper.toDomain(roomEntity, null)
    }

    override fun removePlayer(roomId: String, userId: String) {
        val roomEntity =
            roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: throw NotFoundException("Room not found")
        val playerEntity = roomEntity.players.find { it.user.id == UUID.fromString(userId) }
            ?: throw NotFoundException("Player not found")

        roomEntity.players -= playerEntity

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

        val playerEntity = roomEntity.players.find { it.user.id == UUID.fromString(userId) }
            ?: throw NotFoundException("Player not found")

        val sessionAction = SessionActionEntity(
            instruction = instruction,
            player = playerEntity,
            room = roomEntity
        )

        sessionActionRepository.save(sessionAction)

        roomEntity.actions += sessionAction

        roomsRepository.save(roomEntity)
    }

    override fun delete(roomId: String) {
        roomsRepository.deleteById(UUID.fromString(roomId))
    }

    override fun getUserRooms(userId: UUID): List<Room> {
        val user = usersRepository.findByIdOrNull(userId) ?: return emptyList()
        return roomsRepository.findAllByUser(user).map {
            val status = if (user in it.players.map { player -> player.user }) RoomInvitationStatus.JOINED else  RoomInvitationStatus.PENDING
            mapper.toDomain(it, status)
        }
    }
}