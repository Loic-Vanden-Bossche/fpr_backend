package com.esgi.applicationservices.usecases.groups.message

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class UpdateLastReadUseCase(
    private val groupsPersistence: GroupsPersistence
) {
    operator fun invoke(groupId: UUID, user: User) {
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        groupsPersistence.updateRead(user, group)
    }
}