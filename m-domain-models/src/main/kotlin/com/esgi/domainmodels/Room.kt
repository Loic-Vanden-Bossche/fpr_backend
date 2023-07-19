package com.esgi.domainmodels

import java.time.Instant
import java.util.*

data class Room(
    val id: UUID,
    val owner: User,
    val status: RoomStatus,
    val updatedAt: Instant,
    val createdAt: Instant,
)