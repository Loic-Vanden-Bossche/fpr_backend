package com.esgi.applicationservices

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User


class FindingOneUserByIdUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(id: String): User? {
        return persistence.findById(id)
    }
}