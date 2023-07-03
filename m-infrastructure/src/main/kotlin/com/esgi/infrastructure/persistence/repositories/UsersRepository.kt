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

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) like LOWER(:search) OR LOWER(u.nickname) like LOWER(:search)")
    fun findAllByEmailNickname(search: String): List<UserEntity>

    @Query("SELECT ug FROM GroupEntity g INNER JOIN UserGroupEntity ug ON g.id = ug.group.id WHERE :id = ug.user.id ORDER BY ug.lastRead DESC")
    fun findUserGroups(id: UUID): List<UserGroupEntity>
}