package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.UserGroupEntity
import com.esgi.infrastructure.persistence.entities.UsersGroupsId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersGroupsRepository: CrudRepository<UserGroupEntity, UsersGroupsId>