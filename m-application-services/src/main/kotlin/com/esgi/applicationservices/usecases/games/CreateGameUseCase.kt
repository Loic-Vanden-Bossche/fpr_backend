package com.esgi.applicationservices.usecases.games

import com.esgi.applicationservices.persistence.GamesPersistence
import com.esgi.domainmodels.Game
import com.esgi.domainmodels.User

class CreateGameUseCase(
    private val gamesPersistence: GamesPersistence
) {
    operator fun invoke(
        title: String,
        nbMinPlayers: Int,
        nbMaxPlayers: Int,
        isDeterministic: Boolean,
        owner: User
    ): Game {
        return gamesPersistence.createGame(title, nbMinPlayers, nbMaxPlayers, isDeterministic, owner)
    }
}