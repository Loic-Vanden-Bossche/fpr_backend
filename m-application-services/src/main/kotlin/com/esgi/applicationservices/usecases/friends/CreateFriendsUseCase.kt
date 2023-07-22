package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class CreateFriendsUseCase(
        private val persistence: FriendsPersistence,
        private val userPersistence: UsersPersistence
) {
    fun execute(friendId: UUID, user: User): Boolean {
        if(friendId == user.id){
            return false
        }
        val friend = userPersistence.findById(friendId) ?: run {
            throw NotFoundException("User not found")
        }
        if(persistence.alreadyInRelation(user, friend)){
            return false
        }
        return persistence.createFriend(friend, user)
    }
}