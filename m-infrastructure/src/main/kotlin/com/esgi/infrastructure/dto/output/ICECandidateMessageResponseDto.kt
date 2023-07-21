package com.esgi.infrastructure.dto.output

import com.esgi.infrastructure.dto.input.ICECandidate
import java.util.UUID

data class ICECandidateMessageResponseDto(
    val from: UUID,
    val canditate: ICECandidate
)
