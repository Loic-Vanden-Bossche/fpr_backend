package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.exceptions.NotFoundException

class DeleteRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String) {
        roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")
        roomsPersistence.delete(roomId)
    }
}