package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.domainmodels.User

class FindingAllFriendsUseCase(
        private val persistence: FriendsPersistence
) {
    fun execute(user: User): List<User>{
        return persistence.findAll(user)
    }
}