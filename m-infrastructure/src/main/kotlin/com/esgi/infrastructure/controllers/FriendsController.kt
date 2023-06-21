package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.friends.CreateFriendsUseCase
import com.esgi.applicationservices.usecases.friends.FindingAllFriendsUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CreateFriendDto
import com.esgi.infrastructure.dto.mappers.UserMapper
import com.esgi.infrastructure.dto.output.UserResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
@SecurityRequirement(name = "fpr")
class FriendsController(
        private val findingAllFriendsUseCase: FindingAllFriendsUseCase,
        private val createFriendsUseCase: CreateFriendsUseCase
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
}