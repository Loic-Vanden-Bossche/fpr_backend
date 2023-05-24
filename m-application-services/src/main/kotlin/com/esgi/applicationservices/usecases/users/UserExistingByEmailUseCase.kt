package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence

class UserExistingByEmailUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(email: String): Boolean {
        return persistence.findByEmail(email) != null
    }
}