package com.esgi.applicationservices.services

import java.nio.channels.AsynchronousSocketChannel

interface GameInstanciator {
    fun instanciateGame(gameId: String): AsynchronousSocketChannel
}