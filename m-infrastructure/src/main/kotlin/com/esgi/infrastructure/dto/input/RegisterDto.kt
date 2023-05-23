package com.esgi.infrastructure.dto.input

data class RegisterDto(
    val email: String,
    var username: String,
    val password: String,
)