package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class UsersGroupsId(
        val user: UUID,
        val group: UUID
): Serializable