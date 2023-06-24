package com.esgi.applicationservices.services

import java.nio.channels.AsynchronousSocketChannel

interface GameInstantiator {
    fun instanciateGame(gameId: String): AsynchronousSocketChannel
}