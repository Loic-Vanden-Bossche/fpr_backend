package com.esgi.infrastructure.dto.output

data class GroupResponseDto(
    val id: String,
    val name: String,
    val members: List<UserResponseDto>
)
