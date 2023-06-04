package com.esgi.infrastructure.config

import com.esgi.applicationservices.services.GameInstantiator
import com.esgi.applicationservices.usecases.sessions.StartingSessionUseCase
import com.esgi.applicationservices.usecases.users.*
import com.esgi.infrastructure.persistence.adapters.UsersPersistenceAdapter
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import com.esgi.infrastructure.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder


@Configuration
class ApplicationConfiguration(
    private val usersRepository: UsersRepository,
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
) {
    @Bean
    fun tokensService(@Autowired service: FindingOneUserByEmailUseCase): TokensService {
        return TokensService(jwtDecoder, jwtEncoder, service)
    }

    @Profile("dev")
    @Bean
    fun gameInstantiatorDev(@Autowired dockerService: DockerService, @Autowired tcpService: TcpService): GameInstantiator {
        return GameInstantiatorDev(
            dockerService,
            tcpService
        )
    }

    @Profile("prod")
    @Bean
    @Primary
    fun gameInstantiator(@Autowired tcpService: TcpService): GameInstantiator {
        return GameInstantiatorProd(
            tcpService
        )
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
            usersPersistence(),
        )
    }

    @Bean
    fun registeringUserUseCase(@Autowired service: FindingOneUserByEmailUseCase): RegisteringUserUseCase {
        return RegisteringUserUseCase(
            usersPersistence(),
            service
        )
    }

    @Bean
    fun updatingUserUseCase(): UpdatingUserUseCase {
        return UpdatingUserUseCase(
            usersPersistence(),
            findingUserByIdUseCase()
        )
    }

    @Bean
    fun deletingUserUseCase(): DeletingUserUseCase {
        return DeletingUserUseCase(
            usersPersistence(),
            findingUserByIdUseCase()
        )
    }

    @Bean
    fun creatingUserUseCase(@Autowired service: FindingOneUserByEmailUseCase): CreatingUserUseCase {
        return CreatingUserUseCase(
            usersPersistence(),
            service
        )
    }

    @Bean
    fun createSessionUseCase(@Autowired service: GameInstantiator): StartingSessionUseCase {
        return StartingSessionUseCase(
            service
        )
    }
}