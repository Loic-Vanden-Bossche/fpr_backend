package com.esgi.infrastructure.dto.input

import com.esgi.domainmodels.Role
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Length

data class UpdateUserDto(
    @field:Email
    val email: String?,

    @field:Length(min = 3, max = 30)
    val nickname: String?,

    @field:Length(min = 8, max = 30)
    val password: String?,

    @field:Min(0)
    val coins: Int?,

    @field:Enumerated(value = EnumType.STRING)
    val role: Role?,
)