package com.esgi.infrastructure.persistence.entities

import com.esgi.domainmodels.Role
import com.esgi.infrastructure.persistence.listeners.AuditableDates
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_USERS")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    val email: String,

    @Column(name = "USER_ROLE", nullable = false, columnDefinition = "VARCHAR(255) default 'USER'")
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,

    @Column(name = "USER_NICKNAME", nullable = false)
    val nickname: String,

    @Column(name = "USER_COINS", nullable = false)
    val coins: Int = 0,

    @Column(name = "USER_PASSWORD", nullable = false)
    val password: String
) : AuditableDates()