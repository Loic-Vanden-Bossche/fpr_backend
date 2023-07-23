package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "TJ_GROUPS_USERS")
@IdClass(UsersGroupsId::class)
data class UserGroupEntity(
    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    val user: UserEntity,

    @Id
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "GROUP_ID")
    val group: GroupEntity,

    @Column(name = "LAST_READ", columnDefinition = "TIMESTAMP(6) default NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    var lastRead: Date = Date.from(Instant.now())
)