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

    override fun findById(id: String): User? {
        return userRepository.findById(id).orElse(null)?.let { userEntity ->
            User(
                userEntity.email,
                userEntity.nickname,
                userEntity.password,
                userEntity.coins,
                userEntity.updatedAt,
                userEntity.createdAt,
            )
        }
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let { userEntity ->
            User(
                userEntity.email,
                userEntity.nickname,
                userEntity.password,
                userEntity.coins,
                userEntity.updatedAt,
                userEntity.createdAt,
            )
        }
    }
}