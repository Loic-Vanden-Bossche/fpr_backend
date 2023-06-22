package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.domainmodels.User

class FindingAllPendingFriendsUseCase(
        private val persistence: FriendsPersistence
) {
    fun execute(user: User): List<User> = persistence.findAllPending(user)
}