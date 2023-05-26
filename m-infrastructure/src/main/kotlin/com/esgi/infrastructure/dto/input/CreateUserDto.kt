package com.esgi.infrastructure.dto.input

import com.esgi.domainmodels.Role
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class CreateUserDto(
    @field:Email
    @field:NotNull
    @field:NotEmpty
    @field:Length(max = 255)
    val email: String,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 3, max = 30)
    val nickname: String,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 8, max = 30)
    val password: String,

    @field:NotNull
    @field:Min(0, message = "Coins must be positive")
    val coins: Int,

    @field:NotNull
    @field:Enumerated(value = EnumType.STRING)
    val role: Role,
)