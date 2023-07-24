package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonProperty

data class GameError(
    val type: String,
    val player: Int,
    val subtype: String?,
    val action: Any?,
    @JsonProperty("requested_action")
    val requestedAction: GameRequestedAction,
)