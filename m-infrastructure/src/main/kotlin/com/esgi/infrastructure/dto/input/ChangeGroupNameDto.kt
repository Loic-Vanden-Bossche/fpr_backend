package com.esgi.infrastructure.dto.input

import com.fasterxml.jackson.annotation.JsonCreator
import org.hibernate.validator.constraints.Length

data class ChangeGroupNameDto @JsonCreator constructor(
        @field:Length(min = 3)
        val name: String
)
