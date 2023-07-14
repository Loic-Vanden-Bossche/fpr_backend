package com.esgi.infrastructure.services

import jakarta.inject.Inject
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AuthChannelInterceptorAdapter @Inject constructor(
    private val jwtDecoder: JwtDecoder,
    private val tokensService: TokensService
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        if (accessor?.command == StompCommand.CONNECT) {
            val auth = accessor.getNativeHeader("Authorization") ?: throw InvalidBearerTokenException("Invalid token")
            if (auth.isEmpty()) {
                throw InvalidBearerTokenException("Invalid token")
            }
            val token = jwtDecoder.decode(auth[0].removePrefix("Bearer "))
            JwtAuthenticationConverter().let {
                val jwt = it.convert(token) as JwtAuthenticationToken
                val user =
                    tokensService.parseToken(jwt.token.tokenValue) ?: throw InvalidBearerTokenException("Invalid token")
                accessor.user = UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    listOf(
                        GrantedAuthority {
                            user.role
                                .toString()
                                .uppercase()
                        }
                    )
                )
            }
        }
        return message
    }
}