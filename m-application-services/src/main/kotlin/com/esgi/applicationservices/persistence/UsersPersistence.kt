package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User
import java.util.*

interface UsersPersistence {
    fun findAll(): MutableList<User>
    fun findById(id: UUID): User?
    fun findByEmail(email: String): User?

    fun search(search: String, user: User): List<User>

    fun create(
        email: String,
        nickname: String,
        password: String
    ): User

    fun create(
        email: String,
        nickname: String,
        password: String,
        role: Role,
        coins: Int
    ): User

    fun update(
        id: UUID,
        email: String? = null,
        nickname: String? = null,
        password: String? = null,
        role: Role? = null,
        coins: Int? = null,
        picture: Boolean? = null
    ): User

    fun delete(id: UUID): User
}