package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.entities.SessionActionEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomsRepository : CrudRepository<RoomEntity, UUID> {
    override fun findById(id: UUID): Optional<RoomEntity>

    @Query("SELECT r FROM RoomEntity r INNER JOIN UserGroupEntity ug WHERE ug.user = :user AND r.status <> 'FINISHED'")
    fun findAllByUser(@Param("user") user: UserEntity): List<RoomEntity>

    @Transactional
    @Modifying
    @Query("UPDATE RoomEntity r SET r.status = 'PAUSED' WHERE r.status <> 'FINISHED'")
    fun pauseAllStartedRooms()

    @Query("SELECT r.actions FROM RoomEntity r")
    fun findActionByRoomId(roomId: UUID): List<SessionActionEntity>
}