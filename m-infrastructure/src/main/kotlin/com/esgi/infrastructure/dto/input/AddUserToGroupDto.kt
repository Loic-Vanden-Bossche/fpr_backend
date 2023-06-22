package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AddUserToGroupDto @JsonCreator constructor(
    @field:NotNull
    val users: List<UUID>
)
