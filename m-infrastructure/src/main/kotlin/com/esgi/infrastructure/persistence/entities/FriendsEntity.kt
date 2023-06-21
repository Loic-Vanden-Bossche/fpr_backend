package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_FRIENDS", uniqueConstraints = [UniqueConstraint(columnNames = ["USER_1", "USER_2"])])
data class FriendsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FRIENDS_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "USER_1", nullable = false)
    val user1: UserEntity,

    @ManyToOne
    @JoinColumn(name = "USER_2", nullable = false)
    val user2: UserEntity
)
