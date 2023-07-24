package com.esgi.domainmodels

import java.time.Instant
import java.util.*

data class Room(
    val id: UUID,
    val owner: User,
    val game: Game,
    var status: RoomStatus,
    val updatedAt: Instant,
    val createdAt: Instant,
    val invitationStatus: RoomInvitationStatus?,
    var players: List<Player>,
    val group: Group
)