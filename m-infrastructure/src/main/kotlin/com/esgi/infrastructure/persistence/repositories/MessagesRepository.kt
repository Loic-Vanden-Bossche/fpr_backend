package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.MessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface MessagesRepository: JpaRepository<MessageEntity, UUID> {

    @Query
    fun findAllByGroup(group: GroupEntity, pageable: Pageable): Page<MessageEntity>
}