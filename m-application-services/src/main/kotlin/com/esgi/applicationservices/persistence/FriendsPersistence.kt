package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User

interface FriendsPersistence {
    fun findAll(user: User): List<User>

    fun findAllPending(user: User): List<User>

    fun createFriend(friend: User, user: User): Boolean

    fun setApprove(user: User, friend: User): Boolean

    fun setDeny(user: User, friend: User): Boolean

    fun deleteFriend(user: User, friend: User): Boolean

    fun addGroup(user: User, friend: User, group: Group)

    fun alreadyInRelation(user: User, friend: User): Boolean
}