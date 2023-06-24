package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

@Entity
@DynamicUpdate
@Table(name = "T_MESSAGE")
data class MessageEntity(
    @Id
    @GeneratedValue
    @Column(name = "GROUP_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "MESSAGE", nullable = false, columnDefinition = "TEXT")
    var message: String = "",

    @ManyToOne
    @JoinColumn(name = "GROUP", nullable = false)
    val group: GroupEntity? = null,

    @ManyToOne
    @JoinColumn(name = "USER", nullable = false)
    val user: UserEntity? = null
)
