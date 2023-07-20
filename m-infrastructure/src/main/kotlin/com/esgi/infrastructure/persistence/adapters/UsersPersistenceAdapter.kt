package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.entities.UserEntity
import com.esgi.infrastructure.persistence.repositories.FriendsRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class UsersPersistenceAdapter(
    private val userRepository: UsersRepository,
    private val friendsRepository: FriendsRepository
) : UsersPersistence {
    private val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)

    override fun create(email: String, nickname: String, password: String): User {
        val userToCreate = UserEntity(
            email = email,
            nickname = nickname,
            password = password
        )

        val createdUser = userRepository.save(userToCreate)
        return mapper.toDomain(createdUser, null)
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
        return mapper.toDomain(createdUser, null)
    }

    override fun findAll(): MutableList<User> {
        return userRepository.findAll().map { userEntity ->
            mapper.toDomain(userEntity, null)
        }.toMutableList()
    }

    override fun search(search: String, user: User): List<User> = userRepository.findAllByEmailNickname("%$search%").map{
        val friendsEntity = friendsRepository.findByUserPair(it.id!!, user.id)
        mapper.toDomain(it, friendsEntity?.status)
    }

    override fun findById(id: UUID): User? {
        return userRepository.findByIdOrNull(id)?.let { userEntity ->
            mapper.toDomain(userEntity, null)
        }
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let { userEntity ->
            mapper.toDomain(userEntity, null)
        }
    }

    override fun update(
        id: UUID,
        email: String?,
        nickname: String?,
        password: String?,
        role: Role?,
        coins: Int?,
        picture: Boolean?
    ): User {
        val user = userRepository.findByIdOrNull(id) ?: throw NotFoundException("User not found")

        val userToUpdate = UserEntity(
            id = user.id,
            email = email ?: user.email,
            nickname = nickname ?: user.nickname,
            password = password ?: user.password,
            role = role ?: user.role,
            coins = coins ?: user.coins,
            picture = picture ?: user.picture
        ).apply {
            this.createdAt = user.createdAt
            this.updatedAt = user.updatedAt
        }

        val updatedUser = userRepository.save(userToUpdate)
        return mapper.toDomain(updatedUser, null)
    }

    override fun delete(id: UUID): User {
        val userToDelete =
            userRepository.findByIdOrNull(id) ?: throw NotFoundException("User not found")
        userRepository.delete(userToDelete)
        return mapper.toDomain(userToDelete, null)
    }
}