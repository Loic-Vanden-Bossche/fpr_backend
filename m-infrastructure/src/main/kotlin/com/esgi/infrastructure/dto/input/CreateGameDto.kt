package com.esgi.infrastructure.dto.input;
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateGameDto @JsonCreator constructor(
    @field:NotEmpty
    @field:NotNull
    val title: String,

    @field:NotNull
    @field:Min(1)
    val nbMinPlayers: Int,

    @field:NotNull
    @field:Min(1)
    val nbMaxPlayers: Int,

    @field:NotNull
    val isDeterministic: Boolean,
)
