package com.esgi.domainmodels

import java.util.*

data class Group(
    val id: UUID,
    val name: String,
    val type: GroupType,
    val members: List<UserGroup>,
    val messages: List<Message>,
)
