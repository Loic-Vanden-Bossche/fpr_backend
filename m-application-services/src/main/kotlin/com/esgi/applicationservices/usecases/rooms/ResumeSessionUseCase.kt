package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException

class ResumeSessionUseCase(
    val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String): Room {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")

        if (room.status != RoomStatus.PAUSED) {
            throw BadRequestException("Room not paused")
        }

        return roomsPersistence.updateStatus(roomId, RoomStatus.STARTED)
    }
}