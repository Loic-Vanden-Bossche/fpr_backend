package com.esgi.infrastructure.services

import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.InputStream
import java.net.URI

@Service
class S3Service {
    private lateinit var s3: S3Client
    private lateinit var bucket: String

    fun connect(accessKey: String, secretKey: String, url: String, region: String, bucket: String) {
        this.bucket = bucket
        s3 = S3Client.builder()
            .endpointOverride(URI(url.replace("<region>", region)))
            .credentialsProvider { AwsBasicCredentials.create(accessKey, secretKey) }
            .region(Region.of(region))
            .build()
    }

    @Throws(NoSuchKeyException::class)
    fun getFile(directory: String, key: String): ByteArray =
        s3.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build()
        ).readAllBytes()

    fun putFile(directory: String, key: String, data: ByteArray): Boolean =
        putObject(directory, key, RequestBody.fromBytes(data))

    fun putFile(directory: String, key: String, data: InputStream, length: Long): Boolean =
        putObject(directory, key, RequestBody.fromInputStream(data, length))

    private fun putObject(directory: String, key: String, requestBody: RequestBody): Boolean {
        if (s3.listObjects(ListObjectsRequest.builder().bucket(bucket).prefix("$directory/$key").build())
                .contents().size != 0
        ) {
            return false
        }
        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build(),
            requestBody
        )
        return true
    }

    fun deleteFile(directory: String, key: String): Boolean {
        if (s3.listObjects(ListObjectsRequest.builder().bucket(bucket).prefix("$directory/$key").build())
                .contents().size == 0
        ) {
            return false
        }
        s3.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build()
        )
        return true
    }
}