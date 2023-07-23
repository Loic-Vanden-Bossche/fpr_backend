package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.exceptions.NotFoundException

class StartSessionUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String): Room {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")

        if (room.status != RoomStatus.WAITING) {
            println("Room $roomId is not waiting")
            throw IllegalStateException("Room already started")
        }

        if (room.players.size < room.game.nbMinPlayers) {
            println("Not enough players in room $roomId")
            throw IllegalStateException("Not enough players in room")
        }

        roomsPersistence.calculatePlayerIndexes(roomId)

        return roomsPersistence.updateStatus(roomId, RoomStatus.STARTED)
    }
}