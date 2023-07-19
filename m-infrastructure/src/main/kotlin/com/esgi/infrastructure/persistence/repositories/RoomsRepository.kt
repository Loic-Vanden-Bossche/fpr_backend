package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.RoomEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomsRepository : CrudRepository<RoomEntity, UUID> {
    override fun findById(id: UUID): Optional<RoomEntity>
}