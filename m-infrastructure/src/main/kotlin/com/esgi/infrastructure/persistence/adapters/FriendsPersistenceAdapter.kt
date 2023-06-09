package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.domainmodels.Status
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.entities.FriendsEntity
import com.esgi.infrastructure.persistence.repositories.FriendsRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class FriendsPersistenceAdapter(
        private val friendsRepository: FriendsRepository
): FriendsPersistence {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    override fun findAll(user: User): List<User> = friendsRepository.getAllFriendsOf(UUID.fromString(user.id)).map(mapper::toDomain)

    override fun findAllPending(user: User): List<User> = friendsRepository.getAllPendingOf(UUID.fromString(user.id)).map(mapper::toDomain)

    override fun createFriend(friend: User, user: User): Boolean = try{
        friendsRepository.save(FriendsEntity(user1 = mapper.toEntity(user), user2 = mapper.toEntity(friend), status = Status.PENDING))
        true
    }catch (e: Exception){
        e.printStackTrace()
        false
    }

    override fun setApprove(user: User, friend: User): Boolean {
        val friendPending = friendsRepository.getPendingFromFor(UUID.fromString(user.id), UUID.fromString(friend.id)) ?: return false
        friendPending.status = Status.APPROVED
        friendsRepository.save(friendPending)
        return true
    }

    override fun setDeny(user: User, friend: User): Boolean {
        val friendPending = friendsRepository.getPendingFromFor(UUID.fromString(user.id), UUID.fromString(friend.id)) ?: return false
        friendPending.status = Status.REJECTED
        friendsRepository.save(friendPending)
        return true
    }
}
