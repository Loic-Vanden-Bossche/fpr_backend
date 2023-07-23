package com.esgi.infrastructure.dto.output.games

import java.util.*

data class CreateRoomResponseDto(
    val created: Boolean,
    val id: UUID? = null,
    val reason: String? = null
)