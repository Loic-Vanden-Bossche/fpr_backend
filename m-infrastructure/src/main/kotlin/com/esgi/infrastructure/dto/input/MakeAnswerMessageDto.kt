package com.esgi.infrastructure.dto.input

import java.util.*

data class MakeAnswerMessageDto(
    val group: UUID,
    val answer: RTCDescriptionDto,
    val to: UUID
)
