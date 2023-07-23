package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.User
import java.util.UUID

class FindRoomUseCase(
    private val roomsPersistence: RoomsPersistence
) {
    operator fun invoke(user: User, id: UUID): Room? {
        return roomsPersistence.findByIdOfUser(id, user.id)
    }
}
