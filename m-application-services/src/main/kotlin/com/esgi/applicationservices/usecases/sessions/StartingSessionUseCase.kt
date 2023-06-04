package com.esgi.applicationservices.usecases.sessions

import com.esgi.applicationservices.services.GameInstantiator

class StartingSessionUseCase(
    private val gameInstantiator: GameInstantiator,
) {
    fun execute() {
        gameInstantiator.instanciateGame()
    }
}