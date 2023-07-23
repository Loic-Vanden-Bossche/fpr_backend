package com.esgi.infrastructure.dto.input

import java.util.*

data class CallGroupMessageDto(
    val to: UUID,
    val group: UUID,
    val offer: RTCDescriptionDto
)