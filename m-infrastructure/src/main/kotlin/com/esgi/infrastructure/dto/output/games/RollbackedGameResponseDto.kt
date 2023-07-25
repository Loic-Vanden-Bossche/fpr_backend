package com.esgi.infrastructure.dto.output.games

class RollbackedGameResponseDto(
    val rollbacked: Boolean,
    val reason: String? = null
)