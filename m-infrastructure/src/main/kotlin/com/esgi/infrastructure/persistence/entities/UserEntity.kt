package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@Entity
@Table(name = "T_USERS")
class UserEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    val id: UUID = UUID.randomUUID()

    @Column(name = "USER_EMAIL")
    val email: String = ""

    @Column(name = "USER_NICKNAME")
    val nickname: String = ""

    @Column(name = "USER_COINS")
    val coins = 0

    @Column(name = "USER_PASSWORD")
    val password: String = ""

    @Column(name = "USER_UPDATED_AT")
    val updatedAt = ""

    @Column(name = "USER_CREATED_AT")
    val createdAt = ""
}