package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.profile.GetProfileUseCase
import com.esgi.applicationservices.usecases.users.UpdatingUserUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.UpdateUserDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.mapstruct.factory.Mappers
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "fpr")
class ProfileController(
    private val getProfileUseCase: GetProfileUseCase,
    private val updatingUserUseCase: UpdatingUserUseCase,
) {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    fun getProfile(principal: UsernamePasswordAuthenticationToken): UserResponseDto = mapper.toDto(getProfileUseCase(principal.principal as User))

    @PutMapping("/{id}")
    @ResponseBody
    fun updateProfile(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: String,
    ): UserResponseDto {
        val updatedUser = updatingUserUseCase.execute(
            (principal.principal as User).id,
            id
        )
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(updatedUser)
    }
}