package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.GroupType
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class AddUserToGroupUseCase(
        private val usersPersistence: UsersPersistence,
        private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User, groupId: UUID, usersId: List<UUID>): Group{
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        if(group.type == GroupType.FRIEND){
            throw BadRequestException("Cannot add user to friend group")
        }
        if(user !in group.members.map { it.user }){
            throw NotFoundException("Group not found")
        }
        val users = usersId.distinct().map { usersPersistence.findById(it.toString()) ?: throw NotFoundException("User not found") }.filter { it !in group.members.map { it.user } }
        return groupsPersistence.addUser(group, users)
    }
}