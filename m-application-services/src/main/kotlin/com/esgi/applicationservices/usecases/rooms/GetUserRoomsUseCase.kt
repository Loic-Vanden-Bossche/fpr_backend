package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class GetUserRoomsUseCase(
    private val roomsPersistence: RoomsPersistence,
    private val groupsPersistence: GroupsPersistence,
    private val usersPersistence: UsersPersistence
) {
    operator fun invoke(userId: UUID): List<Room> {
        val user = usersPersistence.findById(userId) ?: throw NotFoundException("User not found")
        val groups = groupsPersistence.findAll(user)
        val rooms = mutableListOf<Room>()
        groups.forEach {
            rooms.addAll(roomsPersistence.getUserRooms(it))
        }
        return rooms
    }
}