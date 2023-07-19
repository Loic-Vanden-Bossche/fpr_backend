package com.esgi.applicationservices.usecases.rooms

import com.esgi.domainmodels.Room
import java.util.*

class CreateRoomUseCase {
    operator fun invoke(): Room {
        println("Creating room...")

        return Room(UUID.randomUUID())
    }
}