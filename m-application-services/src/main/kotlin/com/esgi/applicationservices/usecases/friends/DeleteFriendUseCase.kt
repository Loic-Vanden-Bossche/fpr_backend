package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class DeleteFriendUseCase(
        private val usersPersistence: UsersPersistence,
        private val friendsPersistence: FriendsPersistence
) {
    operator fun invoke(user: User, friendId: UUID){
        val friend = usersPersistence.findById(friendId.toString()) ?: throw NotFoundException("User not found")
        friendsPersistence.setDeny(user, friend)
    }
}