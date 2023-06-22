package com.esgi.infrastructure.config

import com.esgi.applicationservices.services.GameInstantiator
import com.esgi.applicationservices.usecases.sessions.StartingSessionUseCase
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.applicationservices.usecases.friends.CreateFriendsUseCase
import com.esgi.applicationservices.usecases.friends.FindingAllFriendsUseCase
import com.esgi.applicationservices.usecases.games.BuildGameUseCase
import com.esgi.applicationservices.usecases.groups.FindingAllGroupsUseCase
import com.esgi.applicationservices.usecases.users.*
import com.esgi.infrastructure.persistence.adapters.FriendsPersistenceAdapter
import com.esgi.infrastructure.persistence.adapters.GroupPersistenceAdapter
import com.esgi.infrastructure.persistence.adapters.UsersPersistenceAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration(
    private val usersPersistence: UsersPersistenceAdapter,
    private val friendsPersistence: FriendsPersistenceAdapter,
    private val groupsPersistence: GroupPersistenceAdapter
) {
    @Bean
    fun findingAllUsersUseCase(): FindingAllUsersUseCase {
        return FindingAllUsersUseCase(
            usersPersistence
        )
    }

    @Bean
    fun findingUserByIdUseCase(): FindingOneUserByIdUseCase {
        return FindingOneUserByIdUseCase(
            usersPersistence
        )
    }

    @Bean
    fun findingOneUserByEmailUseCase(): FindingOneUserByEmailUseCase {
        return FindingOneUserByEmailUseCase(
            usersPersistence,
        )
    }

    @Bean
    fun registeringUserUseCase(@Autowired service: FindingOneUserByEmailUseCase): RegisteringUserUseCase {
        return RegisteringUserUseCase(
            usersPersistence,
            service
        )
    }

    @Bean
    fun updatingUserUseCase(): UpdatingUserUseCase {
        return UpdatingUserUseCase(
            usersPersistence,
            findingUserByIdUseCase()
        )
    }

    @Bean
    fun deletingUserUseCase(): DeletingUserUseCase {
        return DeletingUserUseCase(
            usersPersistence,
            findingUserByIdUseCase()
        )
    }

    @Bean
    fun creatingUserUseCase(@Autowired service: FindingOneUserByEmailUseCase): CreatingUserUseCase {
        return CreatingUserUseCase(
            usersPersistence,
            service
        )
    }

    @Bean
    fun createSessionUseCase(@Autowired service: GameInstantiator): StartingSessionUseCase {
        return StartingSessionUseCase(
            service
        )
    }

    @Bean
    fun findingAllFriendsUseCase(): FindingAllFriendsUseCase {
        return FindingAllFriendsUseCase(
            friendsPersistence
        )
    }

    @Bean
    fun createFriendUseCase(): CreateFriendsUseCase =
            CreateFriendsUseCase(
                friendsPersistence,
                usersPersistence
            )

    @Bean
    fun findingAllGroupsUseCase(): FindingAllGroupsUseCase =
        FindingAllGroupsUseCase(
            groupsPersistence
        )

    @Bean
    fun buildGameUseCase(
        @Autowired gameBuilder: GameBuilder,
        @Autowired gameUploader: GameUploader
    ): BuildGameUseCase =
        BuildGameUseCase(
            gameUploader,
            gameBuilder
        )
}