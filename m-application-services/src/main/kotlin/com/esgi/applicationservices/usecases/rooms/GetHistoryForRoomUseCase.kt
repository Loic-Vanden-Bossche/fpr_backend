package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.SessionAction
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class GetHistoryForRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: UUID): List<SessionAction> {
        return roomsPersistence.findById(roomId.toString())?.let {
            roomsPersistence.getActionsOfRoom(roomId)
        } ?: throw NotFoundException("Room not found")
    }
}