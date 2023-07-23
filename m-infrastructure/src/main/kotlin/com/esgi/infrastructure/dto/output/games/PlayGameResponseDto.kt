package com.esgi.infrastructure.dto.output.games

data class PlayGameResponseDto(
    val played: Boolean,
    val reason: String? = null
)