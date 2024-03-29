package com.esgi.infrastructure.dto.output

data class PlayerResponseDto(
    val id: String,
    val user: UserResponseDto,
    val playerIndex: Int?,
)