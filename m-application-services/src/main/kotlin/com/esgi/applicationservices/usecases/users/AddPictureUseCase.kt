package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

class AddPictureUseCase(
    private val usersPersistence: UsersPersistence
) {
    operator fun invoke(user: User, data: InputStream, type: String): User {
        val fileName = UUID.randomUUID().toString() + ".$type"
        val file = File(fileName)
        Files.copy(data, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        user.picture?.let {
            File(it).delete()
        }
        return usersPersistence.update(user.id, picture = fileName)
    }
}