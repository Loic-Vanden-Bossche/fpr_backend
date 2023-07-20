package com.esgi.applicationservices.services

import java.io.InputStream

interface GamePictureUploader {
    fun upload(gameId: String, data: InputStream, contentType: String?)
}