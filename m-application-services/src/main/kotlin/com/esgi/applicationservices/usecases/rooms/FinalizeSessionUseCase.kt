package com.esgi.applicationservices.usecases.rooms

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.RoomStatus
import com.esgi.domainmodels.exceptions.NotFoundException

class FinalizeSessionUseCase(
    private val roomsPersistence: RoomsPersistence,
    private val usersPersistence: UsersPersistence
) {
    operator fun invoke(roomId: String, scores: List<Int>) {
        val room = roomsPersistence.findById(roomId) ?: throw NotFoundException("Room not found")

        if (scores.isNotEmpty()) {
            room.players.forEach {
                if (it.playerIndex <= 0) {
                    return@forEach
                }

                usersPersistence.updatePlayerScore(it.user.id.toString(), scores[it.playerIndex - 1])
            }
        }

        if (room.status === RoomStatus.PAUSED) {
            println("Room $roomId is paused, not finishing")
            return
        }

        roomsPersistence.updateStatus(roomId, RoomStatus.FINISHED)
    }
}