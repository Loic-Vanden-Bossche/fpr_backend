package com.esgi.domainmodels

import java.time.Instant
import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val nickname: String,
    val password: String,
    val role: Role,
    val coins: Int,
    val updatedAt: Instant,
    val createdAt: Instant,
    val status: FriendRequestStatus?,
    val picture: Boolean
)
