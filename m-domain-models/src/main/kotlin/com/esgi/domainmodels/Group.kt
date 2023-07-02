package com.esgi.domainmodels

data class Group(
    val id: String,
    val name: String,
    val type: GroupType,
    val members: List<UserGroup>,
    val messages: List<Message>,
)
