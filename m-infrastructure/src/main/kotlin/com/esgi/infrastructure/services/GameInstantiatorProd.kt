package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameInstantiator

class GameInstantiatorProd: GameInstantiator {
    override fun instanciateGame() {
        println("Game instantiated")
    }
}