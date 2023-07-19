package com.esgi.applicationservices.services

import java.io.InputStream

interface ProfilePictureUploader {
    fun upload(userId: String, data: InputStream)
}