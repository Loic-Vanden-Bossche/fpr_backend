package com.esgi.infrastructure.controllers

import com.esgi.*
import com.esgi.applicationservices.usecases.users.*
import com.esgi.domainmodels.User
import com.esgi.infrastructure.config.toUser
import com.esgi.infrastructure.dto.input.CreateUserDto
import com.esgi.infrastructure.dto.input.UpdateUserDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.SearchResponseDto
import com.esgi.infrastructure.dto.output.UserResponseDto
import com.esgi.infrastructure.services.HashService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.mapstruct.factory.Mappers
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
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
    private val getPictureUseCase: GetPictureUseCase,
    private val addPictureUseCase: AddPictureUseCase
) {
    private val mapper: UserMapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    @Secured("ADMIN")
    fun getUsers(): List<UserResponseDto> {
        val users = findingAllUsersUseCase.execute()
        return users.map { user -> mapper.toDto(user) }
    }

    @GetMapping("/{id}/picture")
    @ResponseBody
    fun getUserPicture(@PathVariable id: UUID): ResponseEntity<InputStreamResource> {
        val user = findingOneUserByIdUseCase.execute(id)
        val contentType = when (user?.picture?.split(".")?.last()) {
            "png" -> MediaType.IMAGE_PNG
            "gif" -> MediaType.IMAGE_GIF
            "jpeg" -> MediaType.IMAGE_JPEG
            "jpg" -> MediaType.IMAGE_JPEG
            else -> return ResponseEntity.notFound().build()
        }
        val stream = getPictureUseCase(user) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok()
            .contentType(contentType)
            .body(InputStreamResource(stream))
    }

    @PostMapping("/picture", consumes = ["image/*"])
    @ResponseBody
    @Secured("USER")
    fun addPicture(principal: UsernamePasswordAuthenticationToken, request: HttpServletRequest): UserResponseDto = mapper.toDto(
        addPictureUseCase(principal.principal as User, request.inputStream, request.contentType.split("/")[1])
    )

    @GetMapping("/search/{search}")
    @ResponseBody
    @Secured("USER")
    fun searchUsers(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable @NotNull @NotEmpty search: String
    ): List<SearchResponseDto> =
        searchUserUseCase(search, principal.principal as User).map(mapper::toSearchDto)

    @GetMapping("/{id}")
    @ResponseBody
    @Secured("ADMIN")
    fun getUserById(@PathVariable("id") id: UUID): UserResponseDto {
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