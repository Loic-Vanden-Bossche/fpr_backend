package com.esgi.applicationservices.usecases.users

import com.esgi.applicationservices.persistence.UsersPersistence
import com.esgi.domainmodels.User

class SearchUserUseCase(
    private val usersPersistence: UsersPersistence
) {
    operator fun invoke(search: String, user: User): List<User> = usersPersistence.search(search, user).toMutableList().apply { remove(user) }
}