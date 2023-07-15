package com.esgi.infrastructure.dto.output

import java.util.Date
import java.util.UUID

data class MessageResponseDto(
    val id: UUID,
    val user: UserMessageResponseDto,
    val message: String,
    val createdAt: Date,
    val type: MessageResponseType
)

enum class MessageResponseType {
    NEW,
    EDIT,
    DELETE
}