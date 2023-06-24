package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.MessagesPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Message
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.MessageMapper
import com.esgi.infrastructure.persistence.entities.MessageEntity
import com.esgi.infrastructure.persistence.repositories.GroupsRepository
import com.esgi.infrastructure.persistence.repositories.MessagesRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessagesPersistenceAdapter(
    private val usersRepository: UsersRepository,
    private val groupsRepository: GroupsRepository,
    private val messagesRepository: MessagesRepository
): MessagesPersistence{

    private val mapper = Mappers.getMapper(MessageMapper::class.java)

    override fun findAllInGroup(group: Group, page: Int, size: Int): List<Message> {
        val entity = groupsRepository.findByIdOrNull(UUID.fromString(group.id))!!
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        println(messagesRepository.findAllByGroup(entity, pageable).totalPages)
        return messagesRepository.findAllByGroup(entity, pageable).map(mapper::toDomain).toList()
    }

    override fun findById(message: UUID): Message? {
        return messagesRepository.findByIdOrNull(message)?.let { mapper.toDomain(it) }
    }

    override fun writeMessageToGroup(from: User, group: Group, message: String): Message {
        val groupEntity = groupsRepository.findByIdOrNull(UUID.fromString(group.id))!!
        val userEntity = usersRepository.findByIdOrNull(UUID.fromString(from.id))
        val messageEntity = MessageEntity(message = message, group = groupEntity, user = userEntity)
        return mapper.toDomain(messagesRepository.save(messageEntity))
    }

    override fun editMessage(message: Message, text: String): Message {
        val messageEntity = messagesRepository.findByIdOrNull(message.id)!!
        messageEntity.message = text
        return mapper.toDomain(messagesRepository.save(messageEntity))
    }
}