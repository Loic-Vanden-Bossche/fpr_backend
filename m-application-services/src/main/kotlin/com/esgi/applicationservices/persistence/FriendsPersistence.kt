package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.User

interface FriendsPersistence {
    fun findAll(user: User): List<User>

    fun createFriend(friend: User, user: User): Boolean
}