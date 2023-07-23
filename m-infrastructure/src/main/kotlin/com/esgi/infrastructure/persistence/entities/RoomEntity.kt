package com.esgi.infrastructure.persistence.entities

import com.esgi.domainmodels.RoomStatus
import com.esgi.infrastructure.persistence.listeners.AuditableDates
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_ROOM")
class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ROOM_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "ROOM_STATUS", nullable = false, columnDefinition = "VARCHAR(255) default 'WAITING'")
    @Enumerated(EnumType.STRING)
    var status: RoomStatus = RoomStatus.WAITING,

    @ManyToOne
    @JoinColumn(name = "ROOM_OWNER_ID", referencedColumnName = "USER_ID")
    val owner: UserEntity,

    @ManyToOne
    @JoinColumn(name = "ROOM_GROUP_ID", referencedColumnName = "GROUP_ID")
    val group: GroupEntity,

    @ManyToOne
    @JoinColumn(name = "ROOM_GAME_ID", referencedColumnName = "GAME_ID")
    val game: GameEntity,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "TJ_ROOMS_USERS",
        joinColumns = [JoinColumn(name = "ROOM_ID", referencedColumnName = "ROOM_ID")],
        inverseJoinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")]
    )
    var players: List<UserEntity>,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "TJ_ROOMS_SESSION_ACTIONS",
        joinColumns = [JoinColumn(name = "ROOM_ID", referencedColumnName = "ROOM_ID")],
        inverseJoinColumns = [JoinColumn(name = "ACTION_ID", referencedColumnName = "ACTION_ID")]
    )
    var actions: List<SessionActionEntity>,
) : AuditableDates()