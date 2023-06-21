package com.esgi.applicationservices.usecases.groups

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User

class FindingAllGroupsUseCase(
    private val groupsPersistence: GroupsPersistence
) {
    fun execute(user: User): List<Group> = groupsPersistence.findAll(user)
}