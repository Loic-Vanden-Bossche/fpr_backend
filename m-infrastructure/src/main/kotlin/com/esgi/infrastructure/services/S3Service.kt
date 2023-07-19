package com.esgi.infrastructure.services

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class S3Service {
    private val s3Client: AmazonS3 = AmazonS3Client.builder()
        .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
        .withRegion(Regions.EU_WEST_3)
        .build()

    fun putFile(bucketName: String, fileName: String, data: InputStream) {
        s3Client.putObject(bucketName, fileName, data, null)
    }
}