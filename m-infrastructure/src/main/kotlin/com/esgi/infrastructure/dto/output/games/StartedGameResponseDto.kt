package com.esgi.infrastructure.dto.output.games

data class StartedGameResponseDto(
    val started: Boolean,
    val reason: String? = null
)
