package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateFriendDto @JsonCreator constructor(
    @field:NotNull
    val friend: UUID
)