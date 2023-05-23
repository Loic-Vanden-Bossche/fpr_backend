package com.esgi.infrastructure.controllers

import com.esgi.*
import com.esgi.applicationservices.FindingAllUsersUseCase
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.mapstruct.factory.Mappers
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "fpr")
class UsersController(
    private val findingAllUsersUseCase: FindingAllUsersUseCase,
) {
    @GetMapping
    @ResponseBody
    fun getUsers(): List<UserResponseDto> {
        val users = findingAllUsersUseCase.execute()
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return users.map { user -> mapper.toDto(user) }
    }
}