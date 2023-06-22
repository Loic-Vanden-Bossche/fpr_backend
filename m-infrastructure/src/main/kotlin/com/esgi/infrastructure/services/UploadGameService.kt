package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.esgi.applicationservices.services.GameUploader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class UploadGameService: GameUploader {
    @Value("\${games-bucket.name}")
    private val bucketName: String? = null

    override fun uploadGame(gameId: String, fileStream: InputStream) {
        val s3Client = AmazonS3Client.builder()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.EU_WEST_3)
            .build()

        if (bucketName == null) throw Exception("Bucket name not specified in configuration")

        s3Client.putObject(bucketName, "game-${gameId}", fileStream, null)
    }
}