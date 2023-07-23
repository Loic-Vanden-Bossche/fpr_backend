package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.rooms.GetUserRoomsUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.dto.output.RoomResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rooms")
@SecurityRequirement(name = "fpr")
class RoomsController(
    private val findUserRoomsUseCase: GetUserRoomsUseCase
) {
    private val mapper: RoomMapper = Mappers.getMapper(RoomMapper::class.java)

    @GetMapping
    @ResponseBody
    fun searchUsers(
        principal: UsernamePasswordAuthenticationToken,
    ): List<RoomResponseDto> {
        val rooms = findUserRoomsUseCase(
            (principal.principal as User).id
        )

        println(rooms[0].players)

        return rooms.map { mapper.toDto(it) }
    }

}

