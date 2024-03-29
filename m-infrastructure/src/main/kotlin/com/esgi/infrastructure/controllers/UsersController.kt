package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.users.*
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.input.CreateUserDto
import com.esgi.infrastructure.dto.input.UpdateUserDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.SearchResponseDto
import com.esgi.infrastructure.dto.output.UserResponseDto
import com.esgi.infrastructure.services.HashService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.mapstruct.factory.Mappers
import org.springframework.http.MediaType
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

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
    private val searchUserUseCase: SearchUserUseCase,
    private val addProfilePictureUseCase: AddProfilePictureUseCase
) {
    private val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    @Secured("ADMIN")
    fun getUsers(): List<UserResponseDto> {
        val users = findingAllUsersUseCase.execute()
        return users.map { user -> mapper.toDto(user) }
    }

    @PostMapping("picture", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun addPicture(
        principal: UsernamePasswordAuthenticationToken,
        @RequestPart("file") file: MultipartFile
    ): UserResponseDto = mapper.toDto(
        addProfilePictureUseCase(
            principal.principal as User,
            file.inputStream,
            file.contentType
        )
    )

    @GetMapping("/search/{search}")
    @ResponseBody
    fun searchUsers(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable @NotNull @NotEmpty search: String
    ): List<SearchResponseDto> =
        searchUserUseCase(search, principal.principal as User).map(mapper::toSearchDto)

    @GetMapping("/{id}")
    @ResponseBody
    @Secured("ADMIN")
    fun getUserById(@PathVariable("id") id: UUID): UserResponseDto {
        val user = findingOneUserByIdUseCase.execute(id) ?: throw NotFoundException("User not found")
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
        @PathVariable("id") id: UUID, @RequestBody @Valid updateUserDto: UpdateUserDto
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
    fun deleteUserById(@PathVariable("id") id: UUID): UserResponseDto {
        val deletedUser = deletingUserUseCase.execute(id)
        val mapper = Mappers.getMapper(UserMapper::class.java)

        return mapper.toDto(deletedUser)
    }
}