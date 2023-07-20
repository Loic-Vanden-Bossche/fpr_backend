package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Room
import com.esgi.domainmodels.User

interface RoomsPersistence {
    fun findById(roomId: String): Room?
    fun create(gameId: String, groupId: String, owner: User): Room
}