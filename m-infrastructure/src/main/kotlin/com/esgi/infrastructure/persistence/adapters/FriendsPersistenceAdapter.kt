package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.FriendsPersistence
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.persistence.entities.FriendsEntity
import com.esgi.infrastructure.persistence.repositories.FriendsRepository
import org.mapstruct.factory.Mappers
import java.util.*

class FriendsPersistenceAdapter(
        private val friendsRepository: FriendsRepository
): FriendsPersistence {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    override fun findAll(user: User): List<User> = friendsRepository.getAllFriendsOf(UUID.fromString(user.id)).map(mapper::toDomain)

    override fun createFriend(friend: User, user: User): Boolean = try{
        friendsRepository.save(FriendsEntity(user1 = mapper.toEntity(user), user2 = mapper.toEntity(friend)))
        true
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}
