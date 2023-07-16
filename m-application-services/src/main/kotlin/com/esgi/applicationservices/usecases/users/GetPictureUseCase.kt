package com.esgi.applicationservices.usecases.users

import com.esgi.domainmodels.User
import java.io.File
import java.io.InputStream

class GetPictureUseCase{
    operator fun invoke(user: User): InputStream? {
        return user.picture?.let {
            File(it).inputStream()
        }
    }
}