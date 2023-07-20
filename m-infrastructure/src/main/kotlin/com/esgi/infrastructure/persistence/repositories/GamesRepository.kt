package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GameEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GamesRepository : CrudRepository<GameEntity, UUID> {
    override fun findById(id: UUID): Optional<GameEntity>
}