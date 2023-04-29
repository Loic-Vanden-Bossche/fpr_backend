package com.esgi.infrastructure.config

import com.esgi.applicationservices.FindingAllUsersUseCase
import com.esgi.applicationservices.FindingOneUserByEmailUseCase
import com.esgi.applicationservices.FindingOneUserByIdUseCase
import com.esgi.applicationservices.UserExistingByEmailUseCase
import com.esgi.infrastructure.persistence.adapters.UsersPersistenceAdapter
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import com.esgi.infrastructure.services.TokensService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder


@Configuration
class ApplicationConfiguration(
    private val usersRepository: UsersRepository,
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
) {
    @Bean
    fun tokensService(): TokensService {
        return TokensService(jwtDecoder, jwtEncoder, findingUserByIdUseCase())
    }

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

    @Bean
    fun findingUserByIdUseCase(): FindingOneUserByIdUseCase {
        return FindingOneUserByIdUseCase(
            usersPersistence()
        )
    }

    @Bean
    fun findingOneUserByEmailUseCase(): FindingOneUserByEmailUseCase {
        return FindingOneUserByEmailUseCase(
            usersPersistence()
        )
    }

    @Bean
    fun userExistingByEmailUseCase(): UserExistingByEmailUseCase {
        return UserExistingByEmailUseCase(
            usersPersistence()
        )
    }
}