package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.users.FindingOneUserByEmailUseCase
import com.esgi.applicationservices.usecases.users.RegisteringUserUseCase
import com.esgi.domainmodels.exceptions.LoginFailedException
import com.esgi.infrastructure.dto.input.LoginDto
import com.esgi.infrastructure.dto.input.RegisterDto
import com.esgi.infrastructure.dto.output.LoginResponseDto
import com.esgi.infrastructure.services.HashService
import com.esgi.infrastructure.services.TokensService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val hashService: HashService,
    private val tokenService: TokensService,
    private val findingOneUserByEmailUseCase: FindingOneUserByEmailUseCase,
    private val registeringUserUseCase: RegisteringUserUseCase,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody
        @Valid
        payload: LoginDto
    ): LoginResponseDto {
        val user = findingOneUserByEmailUseCase.execute(payload.email)
            ?: throw LoginFailedException()

        if (!hashService.checkBcrypt(payload.password, user.password)) {
            throw LoginFailedException()
        }

        return LoginResponseDto(tokenService.createToken(user))
    }

    @PostMapping("/register")
    fun register(
        @RequestBody
        @Valid
        payload: RegisterDto
    ): LoginResponseDto {
        val user = registeringUserUseCase.execute(
            payload.email,
            payload.username,
            hashService.hashBcrypt(payload.password),
        )

        return LoginResponseDto(tokenService.createToken(user))
    }

}