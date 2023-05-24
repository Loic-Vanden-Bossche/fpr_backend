package com.esgi.infrastructure.config

import com.esgi.applicationservices.usecases.users.*
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
        return TokensService(jwtDecoder, jwtEncoder, findingOneUserByEmailUseCase())
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

    @Bean
    fun registeringUserUseCase(): RegisteringUserUseCase {
        return RegisteringUserUseCase(
            usersPersistence()
        )
    }

    @Bean
    fun updatingUserUseCase(): UpdatingUserUseCase {
        return UpdatingUserUseCase(
            usersPersistence()
        )
    }

    @Bean
    fun deletingUserUseCase(): DeletingUserUseCase {
        return DeletingUserUseCase(
            usersPersistence()
        )
    }

    @Bean
    fun creatingUserUseCase(): CreatingUserUseCase {
        return CreatingUserUseCase(
            usersPersistence()
        )
    }
}