package com.esgi.domainmodels

import java.util.Date

data class UserGroup(
    val user: User,
    val lastRead: Date
)
