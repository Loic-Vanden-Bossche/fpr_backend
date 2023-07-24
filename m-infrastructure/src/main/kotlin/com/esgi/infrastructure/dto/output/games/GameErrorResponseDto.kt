package com.esgi.infrastructure.dto.output.games

class GameErrorResponseDto(
    val type: String,
    val subtype: String?,
    val action: Any?,
    val requestedAction: GameRequestedActionResponseDto,
)