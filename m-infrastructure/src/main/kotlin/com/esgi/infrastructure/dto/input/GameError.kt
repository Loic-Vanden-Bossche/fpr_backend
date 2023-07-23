package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonProperty

data class GameError(
    val type: String,
    val player: Int,
    @JsonProperty("requested_action")
    val requestedAction: Any,
)