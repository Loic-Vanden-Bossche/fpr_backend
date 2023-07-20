package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.User

class CreateRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(gameId: String, owner: User): Room {
        println("Creating room...")

        return roomsPersistence.create(gameId, owner)
    }
}