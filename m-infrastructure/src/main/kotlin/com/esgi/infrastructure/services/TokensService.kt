package com.esgi.infrastructure.services

import com.esgi.applicationservices.usecases.users.FindingOneUserByEmailUseCase
import com.esgi.domainmodels.User
import org.springframework.security.oauth2.jwt.*
import java.time.Instant
import java.time.temporal.ChronoUnit

class TokensService(
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val findingOneUserByEmailUseCase: FindingOneUserByEmailUseCase,
) {
    fun createToken(user: User): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(30L, ChronoUnit.DAYS))
            .subject(user.email)
            .claim("userId", user.email)
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun parseToken(token: String): User? {
        return try {
            val jwt = jwtDecoder.decode(token)
            val userId = jwt.claims["userId"] as String
            findingOneUserByEmailUseCase.execute(userId)
        } catch (e: Exception) {
            null
        }
    }
}