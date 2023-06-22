package com.esgi.infrastructure.dto.input

import java.util.UUID

data class CreateGroupDto(
        val users: List<UUID>,
        val name: String
)
