package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers

class UsersPersistenceAdapter(private val userRepository: UsersRepository): UsersPersistence {
    override fun create(email: String, nickname: String, password: String): User {
        val mapper = Mappers.getMapper(UserMapper::class.java)
        return mapper.toDomain(userRepository.save(mapper.toEntity(email, nickname, password)))
    }

    override fun findAll(): MutableList<User> {
        val mapper = Mappers.getMapper(UserMapper::class.java)
        return userRepository.findAll().map { userEntity ->
            mapper.toDomain(userEntity)
        }.toMutableList()
    }

    override fun findById(id: String): User? {
        val mapper = Mappers.getMapper(UserMapper::class.java)
        return userRepository.findById(id).orElse(null)?.let { userEntity ->
            mapper.toDomain(userEntity)
        }
    }

    override fun findByEmail(email: String): User? {
        val mapper = Mappers.getMapper(UserMapper::class.java)
        return userRepository.findByEmail(email)?.let { userEntity ->
            mapper.toDomain(userEntity)
        }
    }
}