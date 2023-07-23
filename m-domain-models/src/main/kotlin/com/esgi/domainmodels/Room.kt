package com.esgi.domainmodels

import java.time.Instant
import java.util.*

data class Room(
    val id: UUID,
    val owner: User,
    val game: Game,
    val status: RoomStatus,
    val updatedAt: Instant,
    val createdAt: Instant,
    val players: List<User>,
)