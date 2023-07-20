package com.esgi.infrastructure.dto.output

class GameResponseDto(
    val id: String,
    val title: String,
    val owner: UserResponseDto,
    val picture: Boolean,
)