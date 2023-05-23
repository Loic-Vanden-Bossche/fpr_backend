package com.esgi.infrastructure.dto.input

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class LoginDto(
    @field:NotEmpty
    @field:NotNull
    val email: String,

    @field:NotEmpty
    @field:NotNull
    val password: String,
)