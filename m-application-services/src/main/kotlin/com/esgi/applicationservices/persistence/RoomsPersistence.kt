package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.SessionAction
import com.esgi.domainmodels.User
import java.util.*

interface RoomsPersistence {
    fun findById(roomId: String): Room?
    fun findByIdOfUser(roomId: UUID, user: UUID): Room?
    fun create(gameId: String, groupId: String, owner: User): Room
    fun addPlayer(roomId: String, userId: String)
    fun removePlayer(roomId: String, userId: String)
    fun updateStatus(roomId: String, status: RoomStatus): Room
    fun recordAction(roomId: String, userId: String, instruction: String)
    fun delete(roomId: String)
    fun getUserRooms(userId: UUID): List<Room>
    fun calculatePlayerIndexes(roomId: String): Room
    fun getActionsOfRoom(roomId: UUID): List<SessionAction>
    fun pauseAllRooms()
}