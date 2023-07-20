package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.applicationservices.services.ProfilePictureUploader
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import java.io.InputStream

class AddProfilePictureUseCase(
    private val usersPersistence: UsersPersistence,
    private val profilePictureUploader: ProfilePictureUploader
) {
    private val contentTypes: List<String> = listOf(
        "image/png",
        "image/jpeg",
        "image/gif"
    )

    operator fun invoke(user: User, data: InputStream, contentType: String?): User {
        if (contentType !in contentTypes) {
            throw BadRequestException("Content type $contentType is not allowed")
        }

        profilePictureUploader.upload(user.id.toString(), data, contentType)

        return usersPersistence.update(user.id, picture = true)
    }
}