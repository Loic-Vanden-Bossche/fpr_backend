package com.esgi.infrastructure.dto.output.games

class StoppedGameResponseDto(
    val closed: Boolean,
    val reason: String? = null
)