package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.MessagesPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Message
import com.esgi.infrastructure.dto.mappers.MessageMapper
import com.esgi.infrastructure.persistence.repositories.GroupsRepository
import com.esgi.infrastructure.persistence.repositories.MessagesRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessagesPersistenceAdapter(
    private val groupsRepository: GroupsRepository,
    private val messagesRepository: MessagesRepository
): MessagesPersistence{

    private val mapper = Mappers.getMapper(MessageMapper::class.java)

    override fun findAllInGroup(group: Group): List<Message> {
        val entity = groupsRepository.findByIdOrNull(UUID.fromString(group.id))!!
        return messagesRepository.findAllByGroup(entity).map(mapper::toDomain)
    }
}