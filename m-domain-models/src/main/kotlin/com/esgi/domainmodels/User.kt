package com.esgi.domainmodels

data class User(
    val email: String,
    val nickname: String,
    val password: String,
    val coins: Int,
    val updatedAt: String,
    val createdAt: String,
)
