package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.SessionActionEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SessionActionRepository : CrudRepository<SessionActionEntity, UUID> {
    fun deleteAllByRoomId(roomId: UUID)
}