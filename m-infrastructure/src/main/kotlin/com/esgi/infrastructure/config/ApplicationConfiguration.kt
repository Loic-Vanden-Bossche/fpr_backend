package com.esgi.infrastructure.config

import com.esgi.applicationservices.services.GameInstantiator
import com.esgi.applicationservices.usecases.sessions.StartingSessionUseCase
import com.esgi.applicationservices.services.GameBuilder
import com.esgi.applicationservices.services.GameUploader
import com.esgi.applicationservices.usecases.friends.*
import com.esgi.applicationservices.usecases.games.BuildGameUseCase
import com.esgi.applicationservices.usecases.groups.*
import com.esgi.applicationservices.usecases.groups.message.DeleteMessageInGroupUseCase
import com.esgi.applicationservices.usecases.groups.message.EditMessageInGroupUseCase
import com.esgi.applicationservices.usecases.groups.message.FindingAllGroupMessageUseCase
import com.esgi.applicationservices.usecases.groups.message.WriteMessageToGroupUseCase
import com.esgi.applicationservices.usecases.profile.GetProfileUseCase
import com.esgi.applicationservices.usecases.users.*
import com.esgi.infrastructure.persistence.adapters.FriendsPersistenceAdapter
import com.esgi.infrastructure.persistence.adapters.GroupPersistenceAdapter
import com.esgi.infrastructure.persistence.adapters.MessagesPersistenceAdapter
import com.esgi.infrastructure.persistence.adapters.UsersPersistenceAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.HiddenHttpMethodFilter

@Configuration
class ApplicationConfiguration(
    private val usersPersistence: UsersPersistenceAdapter,
    private val friendsPersistence: FriendsPersistenceAdapter,
    private val groupsPersistence: GroupPersistenceAdapter,
    private val messagesPersistence: MessagesPersistenceAdapter
) {
    @Bean
    fun findingAllUsersUseCase(): FindingAllUsersUseCase {
        return FindingAllUsersUseCase(
            usersPersistence
        )
    }

    @Bean
    fun searchUserUseCase(): SearchUserUseCase =
        SearchUserUseCase(
            usersPersistence
        )

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
    fun getPicutreUseCase(): GetPictureUseCase =
        GetPictureUseCase()

    @Bean
    fun addPictureUseCase(): AddPictureUseCase =
        AddPictureUseCase(
            usersPersistence
        )

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
    fun findingAllPendingFriendUseCase(): FindingAllPendingFriendsUseCase =
        FindingAllPendingFriendsUseCase(
            friendsPersistence
        )

    @Bean
    fun createFriendUseCase(): CreateFriendsUseCase =
        CreateFriendsUseCase(
            friendsPersistence,
            usersPersistence
        )

    @Bean
    fun approveFriendUseCase(): ApproveFriendUseCase =
        ApproveFriendUseCase(
            usersPersistence,
            friendsPersistence,
            groupsPersistence
        )

    @Bean
    fun denyFriendUseCase(): DenyFriendUseCase =
        DenyFriendUseCase(
            usersPersistence,
            friendsPersistence
        )

    @Bean
    fun deleteFriendUseCase(): DeleteFriendUseCase =
            DeleteFriendUseCase(
                usersPersistence,
                friendsPersistence,
                groupsPersistence
            )

    @Bean
    fun findingAllGroupsUseCase(): FindingAllGroupsUseCase =
        FindingAllGroupsUseCase(
            groupsPersistence
        )

    @Bean
    fun findingGroupUseCase(): FindingGroupUseCase =
        FindingGroupUseCase(
            groupsPersistence
        )

    @Bean
    fun createGroupUseCase(): CreateGroupUseCase =
        CreateGroupUseCase(
            usersPersistence,
            groupsPersistence
        )

    @Bean
    fun addUserToGroupUseCase(): AddUserToGroupUseCase =
        AddUserToGroupUseCase(
            usersPersistence,
            groupsPersistence
        )

    @Bean
    fun renameGroupUseCase(): RenameGroupUseCase =
        RenameGroupUseCase(
                groupsPersistence
        )

    @Bean
    fun quitGroupUseCase(): QuitGroupUseCase =
        QuitGroupUseCase(
            groupsPersistence
        )

    @Bean
    fun findingAllGroupMessagesUseCase(): FindingAllGroupMessageUseCase =
        FindingAllGroupMessageUseCase(
            groupsPersistence,
            messagesPersistence
        )

    @Bean
    fun writeMessageToGroupUseCase(): WriteMessageToGroupUseCase =
        WriteMessageToGroupUseCase(
            groupsPersistence,
            messagesPersistence
        )

    @Bean
    fun editMessageInGroupUseCase(): EditMessageInGroupUseCase =
        EditMessageInGroupUseCase(
            groupsPersistence,
            messagesPersistence
        )

    @Bean
    fun deleteMessageInGroupUseCase(): DeleteMessageInGroupUseCase =
        DeleteMessageInGroupUseCase(
            groupsPersistence,
            messagesPersistence
        )

    @Bean
    fun getProfileUseCase(): GetProfileUseCase = GetProfileUseCase()

    @Bean
    fun readGroupUseCase(): ReadGroupUseCase =
        ReadGroupUseCase(
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