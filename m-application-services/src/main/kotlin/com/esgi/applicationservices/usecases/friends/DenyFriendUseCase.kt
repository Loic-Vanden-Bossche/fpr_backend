package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import java.util.UUID

class DenyFriendUseCase(
        private val usersPersistence: UsersPersistence,
        private val friendsPersistence: FriendsPersistence
) {
    fun execute(user: User, friend: UUID): Boolean{
        val friendUser = usersPersistence.findById(friend) ?: run {
            return false
        }
        return friendsPersistence.setDeny(user, friendUser)
    }
}