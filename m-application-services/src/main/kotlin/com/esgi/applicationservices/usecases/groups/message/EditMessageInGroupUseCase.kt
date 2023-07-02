package com.esgi.applicationservices.usecases.groups.message

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.applicationservices.persistence.MessagesPersistence
import com.esgi.domainmodels.Message
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.UUID

class EditMessageInGroupUseCase(
    private val groupsPersistence: GroupsPersistence,
    private val messagesPersistence: MessagesPersistence
) {
    operator fun invoke(user: User, groupId: UUID, messageId: UUID, messageText: String): Message{
        val group = groupsPersistence.find(groupId) ?: throw NotFoundException("Group not found")
        if(user !in group.members.map { it.user }){
            throw  NotFoundException("Group not Found")
        }
        val message = messagesPersistence.findById(messageId) ?: throw NotFoundException("Message not found")
        if(message !in group.messages){
            throw NotFoundException("Message not found in this group")
        }
        return messagesPersistence.editMessage(message, messageText)
    }
}