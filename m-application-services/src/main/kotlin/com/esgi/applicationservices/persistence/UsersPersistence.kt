package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.User

interface UsersPersistence {
    fun findAll(): MutableList<User>
    fun findById(id: String): User?
    fun findByEmail(email: String): User?
    fun create(email: String, nickname: String, password: String): User
}