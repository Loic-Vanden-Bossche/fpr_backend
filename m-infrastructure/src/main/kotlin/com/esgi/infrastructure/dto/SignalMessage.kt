package com.esgi.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SignalMessage<T>(
    val type: String,
    val data: T
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SignalType(
    val type: String
)