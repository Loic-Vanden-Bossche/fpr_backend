package com.esgi.applicationservices

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User

class RegisteringUserUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(email: String, nickname: String, password: String): User {
        return persistence.create(email, nickname, password)
    }
}