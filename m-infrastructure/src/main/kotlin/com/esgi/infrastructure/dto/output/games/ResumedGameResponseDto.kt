package com.esgi.infrastructure.dto.output.games

data class ResumedGameResponseDto(
    val resumed: Boolean,
    val reason: String? = null
)
