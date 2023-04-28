package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : CrudRepository<UserEntity, String>