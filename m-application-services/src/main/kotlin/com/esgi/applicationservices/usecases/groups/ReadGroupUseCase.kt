package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class ReadGroupUseCase(
    private val groupsPersistence: GroupsPersistence
) {
    operator fun invoke(user: User, id: UUID) {
        val group = groupsPersistence.find(id) ?: throw NotFoundException("Group not found")
        groupsPersistence.updateRead(user, group)
    }
}