package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.FindingOneUserByEmailUseCase
import com.esgi.applicationservices.UserExistingByEmailUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.LoginDto
import com.esgi.infrastructure.dto.input.RegisterDto
import com.esgi.infrastructure.dto.output.LoginResponseDto
import com.esgi.infrastructure.services.HashService
import com.esgi.infrastructure.services.TokensService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api")
class AuthController(
    private val hashService: HashService,
    private val tokenService: TokensService,
    private val findingOneUserByEmailUseCase: FindingOneUserByEmailUseCase,
    private val userExistingByEmailUseCase: UserExistingByEmailUseCase
) {
    @PostMapping("/login")
    fun login(@RequestBody payload: LoginDto): LoginResponseDto {
        val user = findingOneUserByEmailUseCase.execute(payload.email) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed")

        if (!hashService.checkBcrypt(payload.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed")
        }

        return LoginResponseDto(
            token = tokenService.createToken(user),
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody payload: RegisterDto): LoginResponseDto {
        if (userExistingByEmailUseCase.execute(payload.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val user = User(
            payload.email,
            nickname = payload.email,
            password = hashService.hashBcrypt(payload.password),
            coins = 0,
            updatedAt = Instant.now(),
            createdAt = Instant.now(),
        )

        return LoginResponseDto(
            token = tokenService.createToken(user),
        )

    }
}