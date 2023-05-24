package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User

class UpdatingUserUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(
        id: String,
        email: String? = null,
        nickname: String? = null,
        password: String? = null,
        role: Role? = null,
        coins: Int? = null,
    ): User {
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