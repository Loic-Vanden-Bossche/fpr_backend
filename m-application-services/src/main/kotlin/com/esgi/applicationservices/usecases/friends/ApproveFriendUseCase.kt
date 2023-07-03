package com.esgi.applicationservices.usecases.friends

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import java.util.UUID

class ApproveFriendUseCase(
        private val usersPersistence: UsersPersistence,
        private val friendsPersistence: FriendsPersistence,
        private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User, friend: UUID): Boolean{
        val friendUser = usersPersistence.findById(friend.toString()) ?: run {
            return false
        }
        val res = friendsPersistence.setApprove(user, friendUser)
        if(res){
            val group = groupsPersistence.createFriendGroup(user, friendUser)
            friendsPersistence.addGroup(user, friendUser, group)
        }
        return res
    }
}