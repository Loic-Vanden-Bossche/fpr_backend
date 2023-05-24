package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User

class DeletingUserUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(userId: String): User {
        return persistence.delete(userId)
    }
}