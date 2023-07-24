package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException

class PauseSessionUseCase(
    val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String) {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")

        when (room.status) {
            RoomStatus.PAUSED -> {
                throw BadRequestException("Room already paused")
            }
            RoomStatus.FINISHED -> {
                throw BadRequestException("Room already finished")
            }
            RoomStatus.WAITING -> {
                throw BadRequestException("Room not started")
            }
            else -> roomsPersistence.updateStatus(roomId, RoomStatus.PAUSED)
        }
    }
}