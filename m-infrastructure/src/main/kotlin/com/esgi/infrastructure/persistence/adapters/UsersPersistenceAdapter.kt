package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.entities.UserEntity
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class UsersPersistenceAdapter(private val userRepository: UsersRepository) : UsersPersistence {
    private val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)

    override fun create(email: String, nickname: String, password: String): User {
        val userToCreate = UserEntity(
            email = email,
            nickname = nickname,
            password = password
        )

        val createdUser = userRepository.save(userToCreate)
        return mapper.toDomain(createdUser)
    }

    override fun create(email: String, nickname: String, password: String, role: Role, coins: Int): User {
        val userToCreate = UserEntity(
            email = email,
            nickname = nickname,
            password = password,
            role = role,
            coins = coins
        )

        val createdUser = userRepository.save(userToCreate)
        return mapper.toDomain(createdUser)
    }

    override fun findAll(): MutableList<User> {
        return userRepository.findAll().map { userEntity ->
            mapper.toDomain(userEntity)
        }.toMutableList()
    }

    override fun search(search: String): List<User> = userRepository.findAllByEmailNickname("%$search%").map(mapper::toDomain)

    override fun findById(id: String): User? {
        return userRepository.findById(UUID.fromString(id)).orElse(null)?.let { userEntity ->
            mapper.toDomain(userEntity)
        }
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let { userEntity ->
            mapper.toDomain(userEntity)
        }
    }

    override fun update(
        id: String,
        email: String?,
        nickname: String?,
        password: String?,
        role: Role?,
        coins: Int?,
    ): User {
        val user = userRepository.findById(UUID.fromString(id)).orElse(null) ?: throw Exception("User not found")

        val userToUpdate = UserEntity(
            id = user.id,
            email = email ?: user.email,
            nickname = nickname ?: user.nickname,
            password = password ?: user.password,
            role = role ?: user.role,
            coins = coins ?: user.coins,
        ).apply {
            this.createdAt = user.createdAt
            this.updatedAt = user.updatedAt
        }

        val updatedUser = userRepository.save(userToUpdate)
        return mapper.toDomain(updatedUser)
    }

    override fun delete(id: String): User {
        val userToDelete =
            userRepository.findById(UUID.fromString(id)).orElse(null) ?: throw Exception("User not found")
        userRepository.delete(userToDelete)
        return mapper.toDomain(userToDelete)
    }
}