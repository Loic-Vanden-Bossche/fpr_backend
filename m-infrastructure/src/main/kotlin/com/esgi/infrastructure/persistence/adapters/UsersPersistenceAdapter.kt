package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.infrastructure.persistence.repositories.UsersRepository

class UsersPersistenceAdapter(private val userRepository: UsersRepository): UsersPersistence {
override fun findAll(): MutableList<User> {
        return userRepository.findAll().map { userEntity ->
            User(
                userEntity.email,
                userEntity.nickname,
                userEntity.password,
                userEntity.coins,
                userEntity.updatedAt,
                userEntity.createdAt,
            )
        }.toMutableList()
    }
}