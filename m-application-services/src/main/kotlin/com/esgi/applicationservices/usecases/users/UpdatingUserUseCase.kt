package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class UpdatingUserUseCase(
    private val persistence: UsersPersistence,
    private val findingOneUserByIdUseCase: FindingOneUserByIdUseCase,
) {
    fun execute(
        id: UUID,
        email: String? = null,
        nickname: String? = null,
        password: String? = null,
        role: Role? = null,
        coins: Int? = null,
    ): User {
        findingOneUserByIdUseCase.execute(id) ?: throw NotFoundException("User with id $id not found")

        return persistence.update(
            id,
            email,
            nickname,
            password,
            role,
            coins
        )
    }
}