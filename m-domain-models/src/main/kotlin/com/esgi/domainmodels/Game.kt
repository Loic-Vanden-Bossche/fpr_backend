package com.esgi.domainmodels

import java.util.UUID

data class Game(
    val id: UUID,
    val title: String,
    val owner: User,
    val picture: Boolean,
)