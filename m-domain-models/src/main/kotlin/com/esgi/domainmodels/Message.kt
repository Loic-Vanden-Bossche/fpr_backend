package com.esgi.domainmodels

import java.util.UUID

data class Message(
    val id: UUID,
    val user: User,
    val message: String
)
