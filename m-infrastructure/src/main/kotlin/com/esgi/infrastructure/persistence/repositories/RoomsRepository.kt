package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.RoomEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

interface RoomWithInvitationStatus {
    fun getRoomEntity(): RoomEntity
    fun getStatus(): String
}

@Repository
interface RoomsRepository : CrudRepository<RoomEntity, UUID> {
    override fun findById(id: UUID): Optional<RoomEntity>

    @Query("""
        SELECT DISTINCT r as roomEntity,
               CASE WHEN (p.id = :userId AND gu.user.id = :userId) THEN 'JOINED'
                    WHEN (gu.user.id = :userId AND p.id IS NULL) THEN 'PENDING'
                    ELSE 'UNKNOWN'
               END as status
        FROM RoomEntity r
        INNER JOIN r.group g 
        INNER JOIN g.users gu 
        LEFT JOIN r.players p 
        WHERE (p.id=:userId OR gu.user.id=:userId)
    """)
    fun findAllByPlayersContains(@Param("userId") userId: UUID): List<RoomWithInvitationStatus>
}