package com.esgi.infrastructure.dto.output.games

class PausedGameResponseDto(
    val paused: Boolean,
    val reason: String? = null
)