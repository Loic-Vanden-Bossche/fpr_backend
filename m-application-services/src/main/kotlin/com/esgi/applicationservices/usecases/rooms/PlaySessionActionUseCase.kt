package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException

class PlaySessionActionUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String, userId: String, action: String) {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room $roomId not found")

        if (room.status != RoomStatus.STARTED) {
            println("Room $roomId is not started")
            throw BadRequestException("Room $roomId is not started")
        }

        roomsPersistence.recordAction(roomId, userId, action)
    }
}