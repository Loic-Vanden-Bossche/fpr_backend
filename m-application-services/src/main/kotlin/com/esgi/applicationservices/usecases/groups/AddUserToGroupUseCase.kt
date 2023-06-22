package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class AddUserToGroupUseCase(
        private val usersPersistence: UsersPersistence,
        private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User, groupId: UUID, usersId: List<UUID>): Group{
        val group = groupsPersistence.find(groupId.toString()) ?: throw NotFoundException("Group not found")
        if(user !in group.members){
            throw NotFoundException("Group not found")
        }
        val users = usersId.map { usersPersistence.findById(it.toString()) ?: throw NotFoundException("User not found") }
        return groupsPersistence.addUser(group, users)
    }
}