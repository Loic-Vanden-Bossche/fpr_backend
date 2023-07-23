package com.esgi.domainmodels

import java.util.*

data class SessionAction(
    val id: UUID,
    val player: User,
    val instruction: String,
    val createdAt: Date,
    val updatedAt: Date,
)