package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.User

class CreateRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(gameId: String, groupId: String, owner: User): Room {
        return roomsPersistence.create(gameId, groupId, owner)
    }
}