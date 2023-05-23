package com.esgi.infrastructure.dto.input

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class RegisterDto(
    @field:Email
    @field:NotEmpty
    @field:NotNull
    @field:Length(max = 255)
    val email: String,

    @field:NotEmpty
    @field:NotNull
    @field:Length(min = 3, max = 30)
    var username: String,

    @field:NotEmpty
    @field:NotNull
    @field:Length(min = 8, max = 30)
    val password: String,
)