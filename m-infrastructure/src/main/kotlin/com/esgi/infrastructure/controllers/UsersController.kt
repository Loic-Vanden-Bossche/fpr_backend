package com.esgi.infrastructure.controllers

import com.esgi.*
import com.esgi.applicationservices.usecases.users.*
import com.esgi.infrastructure.dto.input.CreateUserDto
import com.esgi.infrastructure.dto.input.UpdateUserDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import com.esgi.infrastructure.services.HashService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.UUID
import org.mapstruct.factory.Mappers
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "fpr")
class UsersController(
    private val findingAllUsersUseCase: FindingAllUsersUseCase,
    private val findingOneUserByIdUseCase: FindingOneUserByIdUseCase,
    private val updatingUserUseCase: UpdatingUserUseCase,
    private val hashService: HashService,
    private val deletingUserUseCase: DeletingUserUseCase,
    private val creatingUserUseCase: CreatingUserUseCase,
    private val searchUserUseCase: SearchUserUseCase
) {
    private val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    @Secured("ADMIN")
    fun getUsers(): List<UserResponseDto> {
        val users = findingAllUsersUseCase.execute()
        return users.map { user -> mapper.toDto(user) }
    }

    @GetMapping("/search/{search}")
    @ResponseBody
    @Secured("USER")
    fun searchUsers(@PathVariable @NotNull @NotEmpty search: String): List<UserResponseDto> = searchUserUseCase(search).map(mapper::toDto)

    @GetMapping("/{id}")
    @ResponseBody
    @Secured("ADMIN")
    fun getUserById(@PathVariable("id") @UUID id: String): UserResponseDto {
        val user = findingOneUserByIdUseCase.execute(id) ?: throw Exception("User not found")
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(user)
    }

    @PostMapping
    @ResponseBody
    @Secured("ADMIN")
    fun createUser(@RequestBody @Valid createUserDto: CreateUserDto): UserResponseDto {
        val mapper = Mappers.getMapper(UserMapper::class.java)

        val user = creatingUserUseCase.execute(
            createUserDto.email,
            createUserDto.nickname,
            hashService.hashBcrypt(createUserDto.password),
            createUserDto.role,
            createUserDto.coins,
        )

        return mapper.toDto(user)
    }

    @PutMapping("/{id}")
    @ResponseBody
    @Secured("ADMIN")
    fun updateUserById(
        @PathVariable("id") @UUID id: String, @RequestBody @Valid updateUserDto: UpdateUserDto
    ): UserResponseDto {
        val updatedUser = updatingUserUseCase.execute(
            id,
            updateUserDto.email,
            updateUserDto.nickname,
            updateUserDto.password,
            updateUserDto.role,
            updateUserDto.coins,
        )
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(updatedUser)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @Secured("ADMIN")
    fun deleteUserById(@PathVariable("id") @UUID id: String): UserResponseDto {
        val deletedUser = deletingUserUseCase.execute(id)
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(deletedUser)
    }
}