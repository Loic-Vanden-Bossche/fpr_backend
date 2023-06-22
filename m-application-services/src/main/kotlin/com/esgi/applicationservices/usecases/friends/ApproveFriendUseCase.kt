package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import java.util.UUID

class ApproveFriendUseCase(
        private val usersPersistence: UsersPersistence,
        private val friendsPersistence: FriendsPersistence
) {
    fun execute(user: User, friend: UUID): Boolean{
        val friendUser = usersPersistence.findById(friend.toString()) ?: run {
            return false
        }
        println("friend : $friendUser")
        return friendsPersistence.setApprove(user, friendUser)
    }
}