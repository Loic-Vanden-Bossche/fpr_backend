package com.esgi.domainmodels

data class Player(
    val id: String,
    val user: User,
    val playerIndex: Int
)