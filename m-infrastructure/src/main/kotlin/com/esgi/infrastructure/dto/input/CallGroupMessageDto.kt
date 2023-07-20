package com.esgi.infrastructure.dto.input

import java.util.UUID

data class CallGroupMessageDto(
    val to: UUID,
    val group: UUID,
    val offer: RTCDescriptionDto
)