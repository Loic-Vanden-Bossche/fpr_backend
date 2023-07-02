package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.GroupType
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class RenameGroupUseCase(
        private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User, groupId: UUID, name: String): Group {
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        if(group.type == GroupType.FRIEND){
            throw BadRequestException("Cannot change name of friend group")
        }
        if(user !in group.members.map { it.user } && user.role !== Role.ADMIN){
            throw NotFoundException("Group not found")
        }
        return groupsPersistence.updateGroupName(group, name)
    }
}