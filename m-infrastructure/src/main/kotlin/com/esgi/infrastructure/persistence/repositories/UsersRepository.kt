package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsersRepository : CrudRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
}