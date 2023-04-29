package com.esgi.infrastructure.config

import com.esgi.domainmodels.User
import org.springframework.security.core.Authentication

fun Authentication.toUser(): User {
    return principal as User
}