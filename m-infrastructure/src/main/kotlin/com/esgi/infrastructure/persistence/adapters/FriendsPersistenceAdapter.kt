package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Status
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.entities.FriendsEntity
import com.esgi.infrastructure.persistence.repositories.FriendsRepository
import com.esgi.infrastructure.persistence.repositories.GroupsRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class FriendsPersistenceAdapter(
    private val friendsRepository: FriendsRepository,
    private val groupsRepository: GroupsRepository
): FriendsPersistence {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    override fun findAll(user: User): List<User> = friendsRepository.getAllFriendsOf(user.id).map {
        val userEntity = if(it.user1.id == user.id){
            it.user2
        }else {
            it.user1
        }
        mapper.toDomain(userEntity, it.status)
    }

    override fun findAllPending(user: User): List<User> = friendsRepository.getAllPendingOf(user.id).map {
        val userEntity = if(it.user1.id == user.id){
            it.user2
        }else {
            it.user1
        }
        mapper.toDomain(userEntity, it.status)
    }

    override fun createFriend(friend: User, user: User): Boolean = try{
        friendsRepository.save(FriendsEntity(user1 = mapper.toEntity(user), user2 = mapper.toEntity(friend), status = Status.PENDING))
        true
    }catch (e: Exception){
        e.printStackTrace()
        false
    }

    override fun setApprove(user: User, friend: User): Boolean {
        val friendPending = friendsRepository.getPendingFromFor(user.id, friend.id) ?: return false
        friendPending.status = Status.APPROVED
        friendsRepository.save(friendPending)
        return true
    }

    override fun setDeny(user: User, friend: User): Boolean {
        val friendPending = friendsRepository.getPendingFromFor(user.id, friend.id) ?: return false
        friendPending.status = Status.REJECTED
        friendsRepository.save(friendPending)
        return true
    }

    override fun deleteFriend(user: User, friend: User): Boolean {
        val friendPending = friendsRepository.getFromFor(user.id, friend.id) ?: return false
        friendPending.status = Status.REJECTED
        friendsRepository.save(friendPending)
        return true
    }

    override fun addGroup(user: User, friend: User, group: Group) {
        val friendEntity = friendsRepository.getFromFor(user.id, friend.id)!!
        val groupEntity = groupsRepository.findByIdOrNull(group.id)!!
        friendEntity.group = groupEntity
        friendsRepository.save(friendEntity)
    }
}
