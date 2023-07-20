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
    val picture: Boolean = false,

    @Column(name = "GAME_NB_PLAYERS", nullable = false)
    val nbPlayers: Int,

    @OneToMany(mappedBy = "game")
    val rooms: List<RoomEntity>,
)