package com.esgi.infrastructure.dto.input

import java.util.*

data class AddCandidateMessageDto(
    val to: UUID,
    val candidate: ICECandidate?
)