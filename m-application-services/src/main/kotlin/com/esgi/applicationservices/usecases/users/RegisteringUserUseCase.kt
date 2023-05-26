package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.EmailAlreadyExistsException

class RegisteringUserUseCase(
    private val persistence: UsersPersistence,
    private val findingOneUserByEmailUseCase: FindingOneUserByEmailUseCase,
) {
    fun execute(email: String, nickname: String, password: String): User {
        findingOneUserByEmailUseCase.execute(email)?.let {
            throw EmailAlreadyExistsException(email)
        }
        return persistence.create(email, nickname, password)
    }
}