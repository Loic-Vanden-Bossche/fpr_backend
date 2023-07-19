package com.esgi.applicationservices.usecases.sessions

import com.esgi.applicationservices.services.GameInstanciator

class StartingSessionUseCase(
    private val gameInstanciator: GameInstanciator,
) {
    fun execute() {
        gameInstanciator.instanciateGame("448a82c3-d29c-4921-8b45-480ae59a7cf1")
    }
}