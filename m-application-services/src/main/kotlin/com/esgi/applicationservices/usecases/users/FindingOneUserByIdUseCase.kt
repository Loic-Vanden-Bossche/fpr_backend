package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import java.util.*

class FindingOneUserByIdUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(id: UUID): User? {
        return persistence.findById(id)
    }
}