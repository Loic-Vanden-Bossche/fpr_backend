package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.UserEntity
import com.esgi.infrastructure.persistence.entities.UserGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsersRepository : CrudRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?

    @Query("SELECT u.groups FROM UserEntity u WHERE :id = u.id")
    fun findUserGroups(id: UUID): List<UserGroupEntity>
}