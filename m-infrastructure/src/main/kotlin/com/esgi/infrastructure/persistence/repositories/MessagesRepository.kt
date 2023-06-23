package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.MessageEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface MessagesRepository: CrudRepository<MessageEntity, UUID> {

    @Query
    fun findAllByGroup(group: GroupEntity): List<MessageEntity>
}