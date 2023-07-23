package com.esgi.infrastructure.dto.input

data class GameDisplay(
    val player: Int,
    val width: Int,
    val height: Int,
    val content: List<Any>,
)