package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class JoinRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String, userId: String) {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")

        if (room.players.any { it.user.id == UUID.fromString(userId) }) {
            throw BadRequestException("User is already in the room")
        }

        if (room.players.size >= room.game.nbMaxPlayers) throw BadRequestException("Room is full")

        roomsPersistence.addPlayer(roomId, userId)
    }
}