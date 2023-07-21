package com.esgi.infrastructure.persistence.entities

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@DynamicUpdate
@Table(name = "T_GAMES")
data class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_ID", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    val id: UUID? = null,

    @Column(name = "GAME_TITLE", nullable = false)
    var title: String = "",

    @ManyToOne
    @JoinColumn(name = "GAME_OWNER_ID", referencedColumnName = "USER_ID")
    val owner: UserEntity,

    @Column(name = "GAME_PICTURE", columnDefinition = "BOOLEAN default false")
    var picture: Boolean,

    @Column(name = "GAME_NB_MIN_PLAYERS", nullable = false)
    val nbMinPlayers: Int,

    @Column(name = "GAME_NB_MAX_PLAYERS", nullable = false)
    val nbMaxPlayers: Int,

    @Column(name = "GAME_IS_DETERMINISTIC", nullable = false, columnDefinition = "BOOLEAN default true")
    val isDeterministic: Boolean,

    @Column(name = "GAME_IS_PUBLIC", nullable = false, columnDefinition = "BOOLEAN default false")
    var isPublic: Boolean,

    @Column(name = "GAME_LAST_BUILD_DATE", nullable = true)
    var lastBuildDate: Date?,

    @OneToMany(mappedBy = "game")
    val rooms: List<RoomEntity>,
)