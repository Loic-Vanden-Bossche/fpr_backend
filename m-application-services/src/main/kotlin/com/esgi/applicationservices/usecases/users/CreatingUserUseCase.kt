package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User

class CreatingUserUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(email: String, nickname: String, password: String, role: Role, coins: Int): User {
        return persistence.create(email, nickname, password, role, coins)
    }
}