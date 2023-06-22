package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class FindingGroupUseCase (
    private val groupsPersistence: GroupsPersistence
){
    fun execute(groupId: UUID, user: User): Group {
        val group = groupsPersistence.find(groupId.toString()) ?: throw NotFoundException("Group not found")
        if(user !in group.members && user.role != Role.ADMIN){
            throw NotFoundException("Group not found")
        }
        return group
    }
}