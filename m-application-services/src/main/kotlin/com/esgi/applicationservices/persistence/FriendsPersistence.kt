package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.User
import java.util.UUID

interface FriendsPersistence {
    fun findAll(user: User): List<User>

    fun createFriend(friend: User, user: User): Boolean
}