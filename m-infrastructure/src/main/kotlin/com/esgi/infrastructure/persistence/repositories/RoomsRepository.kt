package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.RoomEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
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
}