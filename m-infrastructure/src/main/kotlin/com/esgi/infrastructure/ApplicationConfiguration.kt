package com.esgi.infrastructure

import com.esgi.applicationservices.FindingAllUsersUseCase
import com.esgi.infrastructure.persistence.adapters.UsersPersistenceAdapter
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ApplicationConfiguration(
    private val usersRepository: UsersRepository,
) {
    @Bean
    fun usersPersistence(): UsersPersistenceAdapter {
        return UsersPersistenceAdapter(usersRepository)
    }

    @Bean
    fun findingAllUsersUseCase(): FindingAllUsersUseCase {
        return FindingAllUsersUseCase(
            usersPersistence()
        )
    }
}