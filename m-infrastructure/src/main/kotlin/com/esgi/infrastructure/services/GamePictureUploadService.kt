package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.GamePictureUploader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class GamePictureUploadService(
    private val s3Service: S3Service
) : GamePictureUploader {
    @Value("\${medias-bucket.name}")
    private val bucketName: String? = null

    override fun upload(gameId: String, data: InputStream, contentType: String?) {
        if (bucketName == null) throw Exception("Bucket name not specified in configuration")

        s3Service.putFile(bucketName, "g/${gameId}", data, contentType)
    }
}