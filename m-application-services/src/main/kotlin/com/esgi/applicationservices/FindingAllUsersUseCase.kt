package com.esgi.applicationservices

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User


class FindingAllUsersUseCase(
    private val persistence: UsersPersistence,
) {
    fun execute(): List<User> {
        return persistence.findAll()
    }
}