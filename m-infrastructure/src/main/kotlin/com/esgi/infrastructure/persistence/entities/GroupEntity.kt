package com.esgi.infrastructure.persistence.entities

import com.esgi.domainmodels.GroupType
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
    var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'FRIEND'")
    var type: GroupType = GroupType.FRIEND,

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    var users: MutableList<UserGroupEntity> = mutableListOf(),

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    val messages: List<MessageEntity> = mutableListOf(),

    @OneToMany(mappedBy = "group")
    val friendsEntity: List<FriendsEntity> = emptyList()
)