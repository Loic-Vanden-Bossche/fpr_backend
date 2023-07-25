package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.*
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
    fun getUserRooms(group: Group): List<Room>
    fun calculatePlayerIndexes(roomId: String): Room
    fun getActionsOfRoom(roomId: UUID): List<SessionAction>
    fun pauseAllRooms()
    fun removeActionsAfterActionId(roomId: String, actionId: String)
}