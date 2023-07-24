package com.esgi.infrastructure.dto.output.games

class GameStateResponseDto(
    val scores: Map<String, Int>,
    val gameOver: Boolean,
)