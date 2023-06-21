package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException

class CreateFriendsUseCase(
        private val persistence: FriendsPersistence,
        private val userPersistence: UsersPersistence
) {
    fun execute(friendId: String, user: User): Boolean {
        if(friendId == user.id){
            return false
        }
        val friend = userPersistence.findById(friendId) ?: run {
            println("Not found")
            throw NotFoundException("User not found")
        }
        println("Found")
        return persistence.createFriend(friend, user)
    }
}