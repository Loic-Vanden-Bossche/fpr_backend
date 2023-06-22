package com.esgi.applicationservices.usecases.sessions

import com.esgi.applicationservices.services.GameInstantiator

class StartingSessionUseCase(
    private val gameInstantiator: GameInstantiator,
) {
    fun execute() {
        gameInstantiator.instanciateGame("448a82c3-d29c-4921-8b45-480ae59a7cf1")
    }
}