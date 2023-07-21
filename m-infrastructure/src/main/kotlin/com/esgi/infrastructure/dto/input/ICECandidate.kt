package com.esgi.infrastructure.dto.input

data class ICECandidate(
    val address: String?,
    val candidate: String?,
    val component: String?,
    val foundation: String?,
    val port: Int?,
    val priority: Int?,
    val protocol: String?,
    val relatedAddress: String?,
    val relatedPort: Int?,
    val sdpMLineIndex: Int?,
    val sdpMid: String?,
    val tcpType: String?,
    val type: String?,
    val usernameFragment: String?
)
