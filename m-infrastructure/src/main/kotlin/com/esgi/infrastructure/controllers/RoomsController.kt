package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.rooms.FindRoomUseCase
import com.esgi.applicationservices.usecases.rooms.GetHistoryForRoomUseCase
import com.esgi.applicationservices.usecases.rooms.GetUserRoomsUseCase
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.dto.output.RoomResponseDto
import com.esgi.infrastructure.dto.output.SessionActionResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/rooms")
@SecurityRequirement(name = "fpr")
class RoomsController(
    private val findUserRoomsUseCase: GetUserRoomsUseCase,
    private val findRoomUseCase: FindRoomUseCase,
    private val getHistoryForRoomUseCase: GetHistoryForRoomUseCase
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

        return rooms.map { mapper.toDto(it) }
    }

    @GetMapping("/{id}")
    @ResponseBody
    fun getById(principal: UsernamePasswordAuthenticationToken, @PathVariable id: UUID): RoomResponseDto {
        return mapper.toDto(
            findRoomUseCase(principal.principal as User, id) ?: throw NotFoundException("Room not found")
        )
    }

    @GetMapping("/{id}/history")
    @ResponseBody
    fun getHistory(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: UUID
    ): List<SessionActionResponseDto> =
        getHistoryForRoomUseCase(id).map { SessionActionResponseDto(it.id, it.player.id, it.instruction) }
}

