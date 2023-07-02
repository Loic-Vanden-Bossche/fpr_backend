package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User
import java.util.UUID

interface GroupsPersistence {
    fun findAll(user: User): List<Group>

    fun create(users: List<User>, name: String): Group

    fun createFriendGroup(user1: User, user2: User): Group

    fun find(id: UUID): Group?

    fun addUser(group: Group, users: List<User>): Group

    fun updateGroupName(group: Group, name: String): Group

    fun removeUserFromGroup(user: User, group: Group): Group?

    fun updateRead(user: User, group: Group)
}