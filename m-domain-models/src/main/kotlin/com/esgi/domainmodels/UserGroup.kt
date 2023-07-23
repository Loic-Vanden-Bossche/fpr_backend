package com.esgi.domainmodels

import java.util.*

data class UserGroup(
    val user: User,
    val lastRead: Date
)
