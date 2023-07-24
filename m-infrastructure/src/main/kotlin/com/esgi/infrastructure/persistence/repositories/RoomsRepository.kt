package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.entities.SessionActionEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomsRepository : CrudRepository<RoomEntity, UUID> {
    override fun findById(id: UUID): Optional<RoomEntity>

    @Query("SELECT r FROM RoomEntity r WHERE r.group = :group")
    fun findAllByGroup(group: GroupEntity): List<RoomEntity>

    @Transactional
    @Modifying
    @Query("UPDATE RoomEntity r SET r.status = 'PAUSED' WHERE r.status <> 'FINISHED'")
    fun pauseAllStartedRooms()

    @Query("SELECT r.actions FROM RoomEntity r")
    fun findActionByRoomId(roomId: UUID): List<SessionActionEntity>
}