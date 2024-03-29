package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class DeletingUserUseCase(
    private val persistence: UsersPersistence,
    private val findingOneUserByIdUseCase: FindingOneUserByIdUseCase,
) {
    fun execute(id: UUID): User {
        findingOneUserByIdUseCase.execute(id) ?: throw NotFoundException("User with id $id not found")
        return persistence.delete(id)
    }
}