package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.applicationservices.services.ProfilePictureUploader
import com.esgi.domainmodels.User
import java.io.InputStream

class AddPictureUseCase(
    private val usersPersistence: UsersPersistence,
    private val profilePictureUploader: ProfilePictureUploader
) {
    operator fun invoke(user: User, data: InputStream, contentType: String): User {
        profilePictureUploader.upload(user.id.toString(), data, contentType)

        return usersPersistence.update(user.id, picture = true)
    }
}