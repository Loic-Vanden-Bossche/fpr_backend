package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonProperty

data class GameState(
    val scores: List<Int>,

    @JsonProperty("game_over")
    val gameOver: Boolean,
)