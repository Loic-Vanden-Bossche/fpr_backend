package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.FriendsEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendsRepository: CrudRepository<FriendsEntity, UUID> {

    @Query("SELECT f FROM FriendsEntity f WHERE (f.user1.id = :id OR f.user2.id = :id) AND f.status = 'APPROVED'")
    fun getAllFriendsOf(id: UUID): List<FriendsEntity>

    @Query("SELECT f FROM FriendsEntity f WHERE f.user2.id = :id AND f.status = 'PENDING'")
    fun getAllPendingOf(id: UUID): List<FriendsEntity>

    @Query("SELECT f FROM FriendsEntity f WHERE f.user1.id = :from AND f.user2.id = :user AND f.status = 'PENDING'")
    fun getPendingFromFor(user: UUID, from: UUID): FriendsEntity?

    @Query("SELECT f FROM FriendsEntity f WHERE ((f.user1.id = :from AND f.user2.id = :user) OR (f.user1.id = :user AND f.user2.id = :from)) AND f.status = 'APPROVED'")
    fun getFromFor(user: UUID, from: UUID): FriendsEntity?

    @Query("SELECT f FROM FriendsEntity f WHERE ((f.user1.id = :from AND f.user2.id = :user) OR (f.user1.id = :user AND f.user2.id = :from)) AND f.status = 'REJECTED'")
    fun getDeniedFromFor(user: UUID, from: UUID): FriendsEntity?

    @Query("SELECT f FROM FriendsEntity f WHERE (f.user1.id = :user1Id AND f.user2.id = :user2Id) OR (f.user2.id = :user1Id AND f.user1.id = :user2Id)")
    fun findByUserPair(user1Id: UUID, user2Id: UUID): FriendsEntity?
}