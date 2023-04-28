package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "T_USERS")
class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID,

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    val email: String,

    @Column(name = "USER_NICKNAME")
    val nickname: String,

    @Column(name = "USER_COINS")
    val coins: Int,

    @Column(name = "USER_PASSWORD")
    val password: String,

    @LastModifiedDate
    @Column(name = "USER_UPDATED_AT")
    val updatedAt: Instant,

    @CreatedDate
    @Column(name = "USER_CREATED_AT")
    val createdAt: Instant
)