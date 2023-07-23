package com.esgi.infrastructure.dto.input

import org.hibernate.validator.constraints.Length
import java.util.*

data class CreateGroupDto(
    val users: List<UUID>,
    @field:Length(min = 3)
    val name: String
)
