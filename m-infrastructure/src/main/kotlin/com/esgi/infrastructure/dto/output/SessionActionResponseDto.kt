package com.esgi.infrastructure.dto.output

import java.util.*

data class SessionActionResponseDto(
    val id: UUID,
    val playerId: UUID,
    val instruction: String
)