package com.esgi.infrastructure.dto.output

import com.esgi.infrastructure.dto.input.RTCDescriptionDto
import java.util.UUID

data class NewUserMessageResponseDto(
    val from: UUID,
    val offer: RTCDescriptionDto
)
