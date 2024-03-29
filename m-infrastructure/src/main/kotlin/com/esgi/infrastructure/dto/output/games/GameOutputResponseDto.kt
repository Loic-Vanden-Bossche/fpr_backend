package com.esgi.infrastructure.dto.output.games

class GameOutputResponseDto(
    val gameState: GameStateResponseDto,
    val display: GameDisplayResponseDto?,
    val requestedActions: List<GameRequestedActionResponseDto>
)