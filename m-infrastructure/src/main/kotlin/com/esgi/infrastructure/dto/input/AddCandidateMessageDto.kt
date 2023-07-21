package com.esgi.infrastructure.dto.input

import java.util.UUID

data class AddCandidateMessageDto(
    val to: UUID,
    val candidate: ICECandidate
)