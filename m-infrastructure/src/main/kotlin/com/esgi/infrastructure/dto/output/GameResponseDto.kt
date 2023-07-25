package com.esgi.infrastructure.dto.output

class GameResponseDto(
    val id: String,
    val title: String,
    val owner: UserResponseDto,
    val picture: Boolean,
    val nbMinPlayers: Int,
    val nbMaxPlayers: Int,
    var isDeterministic: Boolean,
    var isPublic: Boolean,
    val lastBuildDate: String?,
    val needSeed: Boolean
)