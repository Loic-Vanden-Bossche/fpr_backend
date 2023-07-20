package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Game

interface GamesPersistence {
    fun findById(gameId: String): Game?
}