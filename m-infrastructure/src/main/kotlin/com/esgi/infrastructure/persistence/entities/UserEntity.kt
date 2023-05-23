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
    val id: UUID? = null,

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    val email: String,

    @Column(name = "USER_NICKNAME", nullable = false)
    val nickname: String,

    @Column(name = "USER_COINS", nullable = false)
    val coins: Int = 0,

    @Column(name = "USER_PASSWORD", nullable = false)
    val password: String,

    @LastModifiedDate
    @Column(name = "USER_UPDATED_AT", nullable = false)
    val updatedAt: Instant? = null,

    @CreatedDate
    @Column(name = "USER_CREATED_AT", nullable = false)
    val createdAt: Instant? = null,
)