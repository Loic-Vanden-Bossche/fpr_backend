package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonCreator

data class WriteMessageDto @JsonCreator constructor(
    val message: String
)
