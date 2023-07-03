package com.esgi.infrastructure.dto.output

import com.esgi.domainmodels.Status

data class SearchResponseDto(
    val id: String,
    val email: String,
    val nickname: String,
    val status: Status?
)
