package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.groups.*
import com.esgi.applicationservices.usecases.groups.message.FindingAllGroupMessageUseCase
import com.esgi.applicationservices.usecases.groups.message.UpdateLastReadUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.AddUserToGroupDto
import com.esgi.infrastructure.dto.input.ChangeGroupNameDto
import com.esgi.infrastructure.dto.input.CreateGroupDto
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.dto.mappers.MessageMapper
import com.esgi.infrastructure.dto.output.GroupResponseDto
import com.esgi.infrastructure.dto.output.MessageResponseDto
import com.esgi.infrastructure.dto.output.MessageResponseType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.apache.http.HttpStatus
import org.mapstruct.factory.Mappers
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/groups")
@SecurityRequirement(name = "fpr")
class GroupsController(
    private val findingAllGroupsUseCase: FindingAllGroupsUseCase,
    private val findingGroupUseCase: FindingGroupUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val addUserToGroupUseCase: AddUserToGroupUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val quitGroupUseCase: QuitGroupUseCase,
    private val findingAllGroupMessageUseCase: FindingAllGroupMessageUseCase,
    private val readGroupUseCase: ReadGroupUseCase,
    private val updateLastReadUseCase: UpdateLastReadUseCase
) {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)
    private val messageMapper = Mappers.getMapper(MessageMapper::class.java)

    @GetMapping
    @ResponseBody
    fun getGroups(principal: UsernamePasswordAuthenticationToken): List<GroupResponseDto> =
        findingAllGroupsUseCase.execute((principal.principal as User)).map(mapper::toDto)

    @PostMapping
    @ResponseBody
    fun createGroup(
        @RequestBody @Valid body: CreateGroupDto,
        principal: UsernamePasswordAuthenticationToken
    ): GroupResponseDto = mapper.toDto(createGroupUseCase.execute(body.users, principal.principal as User, body.name))

    @GetMapping("/{id}")
    @ResponseBody
    fun getGroup(principal: UsernamePasswordAuthenticationToken, @PathVariable id: UUID): GroupResponseDto =
        mapper.toDto(findingGroupUseCase.execute(id, principal.principal as User))

    @PatchMapping("/{id}")
    @ResponseBody
    fun patchGroup(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: UUID,
        @RequestBody @Valid body: ChangeGroupNameDto
    ): GroupResponseDto =
        mapper.toDto(renameGroupUseCase.execute(principal.principal as User, id, body.name))

    @PutMapping("/{id}")
    @ResponseBody
    fun putMemberInGroup(
        @RequestBody @Valid body: AddUserToGroupDto,
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: UUID
    ): GroupResponseDto =
        mapper.toDto(addUserToGroupUseCase.execute(principal.principal as User, id, body.users))

    @PatchMapping("/{id}/read")
    @Secured("USER")
    fun readGroup(principal: UsernamePasswordAuthenticationToken, @PathVariable @NotNull id: UUID) {
        readGroupUseCase(principal.principal as User, id)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun quitGroup(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: UUID,
        response: HttpServletResponse
    ): GroupResponseDto? =
        quitGroupUseCase.execute(principal.principal as User, id)?.let { mapper.toDto(it) } ?: run {
            response.status = HttpStatus.SC_NO_CONTENT
            null
        }

    @GetMapping("/{id}/messages")
    @ResponseBody
    fun getAllMessages(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable id: UUID,
        @RequestParam page: Int? = null,
        @RequestParam size: Int? = null
    ): List<MessageResponseDto> =
        findingAllGroupMessageUseCase.execute(principal.principal as User, id, page ?: 0, size ?: 20).map {
            messageMapper.toDto(it, MessageResponseType.NEW)
        }.also {
            updateLastReadUseCase(id, principal.principal as User)
        }
}