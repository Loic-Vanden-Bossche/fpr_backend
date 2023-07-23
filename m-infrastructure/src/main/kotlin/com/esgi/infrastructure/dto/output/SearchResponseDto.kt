package com.esgi.infrastructure.dto.output

import com.esgi.domainmodels.FriendRequestStatus
import java.util.*

data class SearchResponseDto(
    val id: UUID,
    val email: String,
    val nickname: String,
    val status: FriendRequestStatus?,
    val picture: Boolean
)
