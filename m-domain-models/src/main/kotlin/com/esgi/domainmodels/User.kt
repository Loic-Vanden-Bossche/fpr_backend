package com.esgi.domainmodels

import java.time.Instant

data class User(
    val email: String,
    val nickname: String,
    val password: String,
    val coins: Int,
    val updatedAt: Instant,
    val createdAt: Instant,
)
