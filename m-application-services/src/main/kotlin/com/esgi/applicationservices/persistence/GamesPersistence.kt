package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Game
import com.esgi.domainmodels.User

interface GamesPersistence {
    fun findAll(): List<Game>

    fun findById(gameId: String): Game?

    fun createGame(
        title: String,
        nbMinPlayers: Int,
        nbMaxPlayers: Int,
        isDeterministic: Boolean,
        owner: User
    ): Game

    fun updateLastBuildDate(gameId: String): Game

    fun setGamePicture(gameId: String): Game
}