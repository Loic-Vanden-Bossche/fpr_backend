package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User

interface GroupsPersistence {
    fun findAll(user: User): List<Group>

    fun create(users: List<User>, name: String): Group
}