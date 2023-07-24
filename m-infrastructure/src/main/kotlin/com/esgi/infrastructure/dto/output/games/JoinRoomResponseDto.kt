package com.esgi.infrastructure.dto.output.games

import java.util.*

data class JoinRoomResponseDto(
    val joined: Boolean,
    val id: UUID? = null,
    val reason: String? = null
)
