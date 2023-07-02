package com.esgi.infrastructure.dto.output

import java.util.*

data class UserGroupResponseDto(
    val lastRead: Date,
    val user: UserResponseDto
)