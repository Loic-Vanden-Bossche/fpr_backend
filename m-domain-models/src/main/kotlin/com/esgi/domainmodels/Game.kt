package com.esgi.domainmodels

import java.util.*

data class Game(
    val id: UUID,
    val title: String,
    val owner: User,
    val picture: Boolean,
    var isPublic: Boolean,
    val nbMinPlayers: Int,
    val nbMaxPlayers: Int,
    var isDeterministic: Boolean,
    val lastBuildDate: Date?,
    var needSeed: Boolean
)