package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence

class PauseAllRoomsUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke() {
        roomsPersistence.pauseAllRooms()
    }
}