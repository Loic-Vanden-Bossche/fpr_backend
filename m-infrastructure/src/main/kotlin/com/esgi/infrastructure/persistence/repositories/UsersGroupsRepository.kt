package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
import com.esgi.infrastructure.persistence.entities.UserGroupEntity
import com.esgi.infrastructure.persistence.entities.UsersGroupsId
import jakarta.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersGroupsRepository : CrudRepository<UserGroupEntity, UsersGroupsId> {

    fun findByGroupAndUser(group: GroupEntity, user: UserEntity): UserGroupEntity

    fun findByGroup(group: GroupEntity): UserGroupEntity

    @Transactional
    fun deleteAllByGroup(group: GroupEntity)
}