package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Role
import com.esgi.domainmodels.User

interface UsersPersistence {
    fun findAll(): MutableList<User>
    fun findById(id: String): User?
    fun findByEmail(email: String): User?

    fun search(search: String): List<User>

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
        id: String,
        email: String?,
        nickname: String?,
        password: String?,
        role: Role?,
        coins: Int?
    ): User

    fun delete(id: String): User
}