package com.esgi.infrastructure.dto.output

data class UserResponseDto(
    val id: String,
    val email: String,
    val role: String,
    val nickname: String,
    val coins: Int,
    val updatedAt: String,
    val createdAt: String,
    val picture: Boolean
)