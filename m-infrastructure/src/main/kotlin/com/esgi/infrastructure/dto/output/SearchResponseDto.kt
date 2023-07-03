package com.esgi.infrastructure.dto.output

import com.esgi.domainmodels.Status
import java.util.UUID

data class SearchResponseDto(
    val id: UUID,
    val email: String,
    val nickname: String,
    val status: Status?
)
