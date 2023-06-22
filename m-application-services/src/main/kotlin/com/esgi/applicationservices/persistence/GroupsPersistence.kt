package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User

interface GroupsPersistence {
    fun findAll(user: User): List<Group>

    fun create(users: List<User>, name: String): Group

    fun find(id: String): Group?

    fun addUser(group: Group, users: List<User>): Group

    fun updateGroupName(group: Group, name: String): Group
}