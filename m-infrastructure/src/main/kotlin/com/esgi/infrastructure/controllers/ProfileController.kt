package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.profile.GetProfileUseCase
import com.esgi.applicationservices.usecases.profile.UpdateProfileNickName
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.UpdateProfileNickNameDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "fpr")
class ProfileController(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileNickName: UpdateProfileNickName
) {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    fun getProfile(principal: UsernamePasswordAuthenticationToken): UserResponseDto = mapper.toDto(getProfileUseCase(principal.principal as User))

    @PatchMapping
    @ResponseBody
    fun updateProfile(
        principal: UsernamePasswordAuthenticationToken,
        @RequestBody @Valid body: UpdateProfileNickNameDto
    ): UserResponseDto {
        val updatedUser = updateProfileNickName(
            (principal.principal as User).id,
            body.nickname
        )
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(updatedUser)
    }
}