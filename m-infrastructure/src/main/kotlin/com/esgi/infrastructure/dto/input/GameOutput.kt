package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonProperty

data class GameOutput(
    @JsonProperty("game_state")
    val gameState: GameState,

    val displays: List<GameDisplay>,

    @JsonProperty("requested_actions")
    val requestedActions: List<GameRequestedAction>,
)