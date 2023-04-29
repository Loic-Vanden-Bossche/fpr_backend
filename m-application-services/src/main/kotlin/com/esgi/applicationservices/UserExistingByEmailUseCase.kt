package com.esgi.applicationservices

import com.esgi.applicationservices.persistence.UsersPersistence

class UserExistingByEmailUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(email: String): Boolean {
        return persistence.findByEmail(email) != null
    }
}