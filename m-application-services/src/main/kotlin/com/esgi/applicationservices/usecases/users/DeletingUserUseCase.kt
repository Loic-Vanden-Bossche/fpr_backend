package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User

class DeletingUserUseCase(
    private val persistence: UsersPersistence,
    private val findingOneUserByIdUseCase: FindingOneUserByIdUseCase,
) {
    fun execute(id: String): User {
        findingOneUserByIdUseCase.execute(id) ?: throw Exception("User with id $id not found")
        return persistence.delete(id)
    }
}