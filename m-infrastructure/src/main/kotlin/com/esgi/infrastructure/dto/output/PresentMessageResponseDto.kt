package com.esgi.infrastructure.dto.output

import java.util.UUID

data class PresentMessageResponseDto(
    val presents: List<UUID>
)
