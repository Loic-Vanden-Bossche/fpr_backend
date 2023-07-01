package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.profile.GetProfileUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.mapstruct.factory.Mappers
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "fpr")
class ProfileController(
        private val getProfileUseCase: GetProfileUseCase
) {
    private val mapper = Mappers.getMapper(UserMapper::class.java)
    @GetMapping
    @Secured("USER")
    @ResponseBody
    fun getProfile(principal: UsernamePasswordAuthenticationToken): UserResponseDto = mapper.toDto(getProfileUseCase(principal.principal as User))
}