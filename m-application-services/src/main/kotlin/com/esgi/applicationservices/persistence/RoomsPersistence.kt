package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Room

interface RoomsPersistence {
    fun findById(roomId: String): Room?
}