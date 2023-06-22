package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.FriendsEntity
import com.esgi.infrastructure.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendsRepository: CrudRepository<FriendsEntity, UUID> {

    @Query("SELECT u FROM UserEntity u INNER JOIN FriendsEntity f ON f.user1.id = u.id or f.user2.id = u.id WHERE u.id <> :id AND (f.user1.id = :id OR f.user2.id = :id) AND f.status = 'APPROVED'")
    fun getAllFriendsOf(id: UUID): List<UserEntity>

    @Query("SELECT u FROM UserEntity u INNER JOIN FriendsEntity f on f.user1.id = u.id or f.user2.id = u.id WHERE u.id <> :id AND (f.user1.id = :id OR f.user2.id = :id) AND f.status = 'PENDING'")
    fun getAllPendingOf(id: UUID): List<UserEntity>

    @Query("SELECT f FROM FriendsEntity f WHERE f.user1.id = :from AND f.user2.id = :user")
    fun getPendingFromFor(user: UUID, from: UUID): FriendsEntity?
}