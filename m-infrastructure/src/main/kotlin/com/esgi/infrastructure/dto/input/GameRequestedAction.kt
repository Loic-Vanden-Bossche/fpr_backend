package com.esgi.infrastructure.dto.input

data class GameRequestedAction(
    val type: String,
    val player: Int,
    val zones: List<Any>,
)