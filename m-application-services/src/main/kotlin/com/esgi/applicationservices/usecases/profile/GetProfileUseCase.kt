package com.esgi.applicationservices.usecases.profile

import com.esgi.domainmodels.User

class GetProfileUseCase {
    operator fun invoke(user: User): User = user
}