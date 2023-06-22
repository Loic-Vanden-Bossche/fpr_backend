package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class CreateGroupUseCase(
        private val usersPersistence: UsersPersistence,
        private val groupsPersistence: GroupsPersistence
) {
    fun execute(usersId: List<UUID>, currentUser: User, name: String): Group {
        var hasUser = false
        val users = usersId.map {
            if(it.toString() != currentUser.id){
                usersPersistence.findById(it.toString()) ?: throw NotFoundException("User Not Found")
            }else {
                hasUser = true
                currentUser
            }
        }.toMutableList()
        if(!hasUser){
            users.add(currentUser)
        }
        return groupsPersistence.create(users, name)
    }
}