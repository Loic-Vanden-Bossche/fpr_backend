package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import java.util.UUID

class GetUserRoomsUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(userId: UUID): List<Room> {
        return roomsPersistence.getUserRooms(userId)
    }
}