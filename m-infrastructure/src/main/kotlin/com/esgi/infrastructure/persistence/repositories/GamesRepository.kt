package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GameEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GamesRepository : CrudRepository<GameEntity, UUID> {
    override fun findById(id: UUID): Optional<GameEntity>

    fun findAllByLastBuildDateIsNotNull(): List<GameEntity>

    fun findAllByOwner(owner: UserEntity): List<GameEntity>
}