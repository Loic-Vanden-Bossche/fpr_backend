package com.esgi.infrastructure.persistence.entities

import com.esgi.infrastructure.persistence.listeners.AuditableDates
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_SESSION_ACTIONS")
data class SessionActionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ACTION_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "ACTION_INSTRUCTION", nullable = false)
    var instruction: String = "",

    @ManyToOne
    @JoinColumn(name = "ACTION_PLAYER", referencedColumnName = "USER_ID")
    val player: UserEntity,
): AuditableDates()