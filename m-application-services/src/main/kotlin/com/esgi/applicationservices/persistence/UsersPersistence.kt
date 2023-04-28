package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.User

interface UsersPersistence {
    fun findAll(): MutableList<User>
}