package com.esgi.applicationservices.usecases.profile

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import java.util.*

class UpdateProfileNickName(
    private val usersPersistence: UsersPersistence
) {
    operator fun invoke(userId: UUID, newNickName: String): User {
        val user = usersPersistence.findById(userId) ?: throw NotFoundException("User not found")

        return usersPersistence.updateNickName(user.id, newNickName)
    }
}