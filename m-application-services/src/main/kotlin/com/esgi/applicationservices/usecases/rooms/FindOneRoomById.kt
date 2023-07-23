package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.exceptions.NotFoundException

class FindOneRoomById(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: String): Room {
        return roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")
    }
}