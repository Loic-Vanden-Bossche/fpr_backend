package com.esgi.infrastructure.dto.output

import com.esgi.domainmodels.RoomInvitationStatus

data class RoomResponseDto(
    val id: String,
    val status: String,
    val players: List<PlayerResponseDto>,
    val invitationStatus: RoomInvitationStatus?,
    val game: GameResponseDto,
    val group: GroupResponseDto
)