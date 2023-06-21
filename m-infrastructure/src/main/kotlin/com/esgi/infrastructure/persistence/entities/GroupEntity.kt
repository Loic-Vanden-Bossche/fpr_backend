package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_GROUPS")
data class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GROUP_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "GROUP_NAME", nullable = false)
    val name: String = "",

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "TJ_GROUPS_USERS",
        joinColumns = [JoinColumn(name = "GROUP_ID")],
        inverseJoinColumns = [JoinColumn(name = "USER_ID")]
    )
    var users: List<UserEntity>? = emptyList()
)