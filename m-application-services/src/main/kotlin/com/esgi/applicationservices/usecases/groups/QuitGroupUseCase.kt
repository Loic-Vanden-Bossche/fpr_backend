package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.GroupType
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class QuitGroupUseCase(
    private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User, groupId: UUID): Group? {
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        if(group.type == GroupType.FRIEND){
            throw BadRequestException("Cannot leave friend group")
        }
        if(user !in group.members){
            throw NotFoundException("Group not found")
        }
        return groupsPersistence.removeUserFromGroup(user, group)
    }
}