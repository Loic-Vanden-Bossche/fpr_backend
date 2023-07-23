package com.esgi.domainmodels

import java.util.*

data class Message(
    val id: UUID,
    val user: User,
    val message: String,
    val createdAt: Date
)
