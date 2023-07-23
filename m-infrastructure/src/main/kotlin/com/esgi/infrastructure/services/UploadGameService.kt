package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GameUploader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class UploadGameService(
    val s3Service: S3Service
) : GameUploader {
    @Value("\${games-bucket.name}")
    private val bucketName: String? = null

    override fun uploadGame(gameId: String, fileStream: InputStream) {
        if (bucketName == null) throw Exception("Bucket name not specified in configuration")

        s3Service.putFile(bucketName, "game-${gameId}", fileStream)
    }
}