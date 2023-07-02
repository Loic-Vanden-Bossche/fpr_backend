package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.friends.*
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CreateFriendDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/friends")
@SecurityRequirement(name = "fpr")
class FriendsController(
        private val findingAllFriendsUseCase: FindingAllFriendsUseCase,
        private val findingAllPendingFriendsUseCase: FindingAllPendingFriendsUseCase,
        private val createFriendsUseCase: CreateFriendsUseCase,
        private val approveFriendUseCase: ApproveFriendUseCase,
        private val denyFriendUseCase: DenyFriendUseCase,
        private val deleteFriendUseCase: DeleteFriendUseCase
) {
    private val mapper = Mappers.getMapper(UserMapper::class.java)

    @GetMapping
    @ResponseBody
    @Secured("USER")
    fun getFriends(principal: UsernamePasswordAuthenticationToken): List<UserResponseDto> = findingAllFriendsUseCase.execute((principal.principal as User)).map(mapper::toDto)

    @PostMapping
    @ResponseBody
    @Secured("USER")
    fun createFriends(@RequestBody @Valid createFriendDto: CreateFriendDto, principal: UsernamePasswordAuthenticationToken, response: HttpServletResponse){
        if(!createFriendsUseCase.execute(createFriendDto.friend.toString(), principal.principal as User)){
            response.status = HttpStatus.NO_CONTENT.value()
        }
    }

    @GetMapping("/pending")
    @ResponseBody
    @Secured("USER")
    fun getPendingFriends(principal: UsernamePasswordAuthenticationToken): List<UserResponseDto> = findingAllPendingFriendsUseCase.execute((principal.principal as User)).map(mapper::toDto)

    @PatchMapping("/{id}/approve")
    @Secured("USER")
    fun setApproveFriend(principal: UsernamePasswordAuthenticationToken, @PathVariable @NotNull id: UUID, response: HttpServletResponse){
        if(!approveFriendUseCase.execute(principal.principal as User, id)){
            response.status = HttpStatus.NOT_FOUND.value()
        }
    }

    @PatchMapping("/{id}/deny")
    @Secured("USER")
    fun setDeniedFriend(principal: UsernamePasswordAuthenticationToken, @PathVariable @NotNull id: UUID, response: HttpServletResponse){
        if(!denyFriendUseCase.execute(principal.principal as User, id)){
            response.status = HttpStatus.NOT_FOUND.value()
        }
    }

    @DeleteMapping("/{id}")
    @Secured("USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFriend(principal: UsernamePasswordAuthenticationToken, @PathVariable @NotNull id: UUID){
        deleteFriendUseCase(principal.principal as User, id)
    }
}