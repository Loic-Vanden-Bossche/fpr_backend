package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.SessionAction
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class RollbackSessionUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(roomId: UUID, actionId: String): List<SessionAction> {
        roomsPersistence.findById(roomId.toString()) ?: throw NotFoundException("Room $roomId not found")

        roomsPersistence.removeActionsAfterActionId(roomId.toString(), actionId)

        return roomsPersistence.getActionsOfRoom(roomId)
    }
}