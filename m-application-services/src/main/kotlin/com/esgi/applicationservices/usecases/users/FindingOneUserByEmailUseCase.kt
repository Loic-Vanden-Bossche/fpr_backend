package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User

class FindingOneUserByEmailUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(email: String): User? {
        return persistence.findByEmail(email)
    }
}