package com.esgi.infrastructure.dto.output

class GameResponseDto(
    val id: String,
    val title: String,
    val owner: UserResponseDto,
    val picture: Boolean,
    val nbMinPlayers: Int,
    val nbMaxPlayers: Int,
    val isDeterministic: Boolean,
    val isPublic: Boolean,
    val lastBuildDate: String?,
)