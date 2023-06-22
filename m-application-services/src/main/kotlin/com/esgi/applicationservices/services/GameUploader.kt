package com.esgi.applicationservices.services

import java.io.InputStream

interface GameUploader {
    fun uploadGame(gameId: String, fileStream: InputStream)
}