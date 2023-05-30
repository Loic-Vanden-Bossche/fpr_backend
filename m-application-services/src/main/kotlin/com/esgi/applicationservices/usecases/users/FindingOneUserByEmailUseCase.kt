package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.applicationservices.services.GameInstantiator
import com.esgi.domainmodels.User


class FindingOneUserByEmailUseCase(
    private val persistence: UsersPersistence,
    private val gameInstantiator: GameInstantiator
) {
    fun execute(email: String): User? {

        gameInstantiator.instanciateGame()

        return persistence.findByEmail(email)
    }
}