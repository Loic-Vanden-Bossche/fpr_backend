package com.esgi.infrastructure.config

import com.esgi.infrastructure.services.TokensService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val tokenService: TokensService,
    private val authFailedEntryPointJwt: AuthFailedEntryPointJwt,
    private val roleAccessDeniedHandler: RoleAccessDeniedHandler,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/socket/**").permitAll()
                .requestMatchers("/webrtc/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sessions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/picture").permitAll()
                .requestMatchers("/api/**").authenticated()
        }

        http.exceptionHandling {
            it
                .authenticationEntryPoint(authFailedEntryPointJwt)
                .accessDeniedHandler(roleAccessDeniedHandler)
        }

        http.oauth2ResourceServer { oauth2ResourceServer ->
            oauth2ResourceServer.jwt {
                it.authenticationManager { auth ->
                    val jwt = auth as BearerTokenAuthenticationToken
                    val user = tokenService.parseToken(jwt.token) ?: throw InvalidBearerTokenException("Invalid token")
                    UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        listOf(
                            SimpleGrantedAuthority(
                                user.role
                                    .toString()
                                    .uppercase()
                            )
                        )
                    )
                }
            }
        }

        http.csrf { csrf ->
            csrf.disable()
        }

        http.cors { cors ->
            cors.configurationSource {
                val configuration = CorsConfiguration()
                configuration.allowedOrigins = listOf(
                    "http://localhost:8080",
                    "https://jxy.me",
                    "http://localhost:5173",
                    "https://flash-player-revival.net",
                    "http://192.168.1.67:5173"
                )
                configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                configuration.allowedHeaders = listOf("authorization", "content-type")
                configuration.allowCredentials = true
                configuration
            }
        }

        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        return http.build()
    }
}