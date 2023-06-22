package com.esgi.infrastructure.dto.output

import com.esgi.domainmodels.GroupType

data class GroupResponseDto(
    val id: String,
    val name: String,
    val type: GroupType,
    val members: List<UserResponseDto>
)
