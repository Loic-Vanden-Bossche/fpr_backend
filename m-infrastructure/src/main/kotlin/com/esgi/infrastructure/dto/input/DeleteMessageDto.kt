package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.UUID

data class DeleteMessageDto @JsonCreator constructor(
    val id: UUID
)