package com.esgi.infrastructure.dto.output

import java.util.UUID

data class MessageResponseDto(
    val id: UUID,
    val user: UserMessageResponseDto,
    val message: String
)
