package com.esgi.domainmodels

data class Group(
    val id: String,
    val name: String,
    val type: GroupType,
    val members: List<User>
)
