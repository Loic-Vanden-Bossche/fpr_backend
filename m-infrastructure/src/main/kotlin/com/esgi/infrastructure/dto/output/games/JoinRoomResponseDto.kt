package com.esgi.infrastructure.dto.output.games

data class JoinRoomResponseDto(
    val joined: Boolean,
    val reason: String? = null
)
