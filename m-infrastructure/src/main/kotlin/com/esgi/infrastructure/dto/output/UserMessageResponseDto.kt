package com.esgi.infrastructure.dto.output

import java.util.*

data class UserMessageResponseDto(
    val id: UUID,
    val email: String,
    val nickname: String
)
