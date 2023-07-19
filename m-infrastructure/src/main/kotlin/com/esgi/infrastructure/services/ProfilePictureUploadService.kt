package com.esgi.infrastructure.services

import com.esgi.applicationservices.services.ProfilePictureUploader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class ProfilePictureUploadService(
    private val s3Service: S3Service
): ProfilePictureUploader {
    @Value("\${medias-bucket.name}")
    private val bucketName: String? = null

    override fun upload(userId: String, data: InputStream, contentType: String) {
        if (bucketName == null) throw Exception("Bucket name not specified in configuration")

        s3Service.putFile(bucketName, "p/${userId}", data)
    }
}