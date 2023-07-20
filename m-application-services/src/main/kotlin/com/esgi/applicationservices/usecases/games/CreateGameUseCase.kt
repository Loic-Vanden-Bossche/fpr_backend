package com.esgi.applicationservices.usecases.games

import com.esgi.domainmodels.Game
import com.esgi.domainmodels.User

class CreateGameUseCase {
    operator fun invoke(
        title: String,
        nbMinPlayers: Int,
        nbMaxPlayers: Int,
        isDeterministic: Boolean,
        owner: User
    ): Game {
        println(owner)
        println(title)
        TODO()
    }
}