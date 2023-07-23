package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.PlayerEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlayersRepository : CrudRepository<PlayerEntity, UUID>