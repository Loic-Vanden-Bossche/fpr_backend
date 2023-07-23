package com.esgi.applicationservices.usecases.groups.message

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.MessagesPersistence
import com.esgi.domainmodels.Message
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class FindingAllGroupMessageUseCase(
    private val groupsPersistence: GroupsPersistence,
    private val messagesPersistence: MessagesPersistence
) {
    fun execute(user: User, groupId: UUID, page: Int = 0, size: Int = 20): List<Message> {
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        if (user !in group.members.map { it.user }) {
            throw NotFoundException("Group not found")
        }
        return messagesPersistence.findAllInGroup(group, page, size)
    }
}