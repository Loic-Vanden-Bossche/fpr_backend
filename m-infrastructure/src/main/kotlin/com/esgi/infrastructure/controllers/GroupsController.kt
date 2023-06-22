package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.groups.AddUserToGroupUseCase
import com.esgi.applicationservices.usecases.groups.FindingAllGroupsUseCase
import com.esgi.applicationservices.usecases.groups.CreateGroupUseCase
import com.esgi.applicationservices.usecases.groups.FindingGroupUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.AddUserToGroupDto
import com.esgi.infrastructure.dto.input.CreateGroupDto
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.dto.output.GroupResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/groups")
@SecurityRequirement(name = "fpr")
class GroupsController(
    private val findingAllGroupsUseCase: FindingAllGroupsUseCase,
    private val findingGroupUseCase: FindingGroupUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val addUserToGroupUseCase: AddUserToGroupUseCase
) {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)

    @GetMapping
    @ResponseBody
    fun getGroups(principal: UsernamePasswordAuthenticationToken): List<GroupResponseDto> = findingAllGroupsUseCase.execute((principal.principal as User)).map(mapper::toDto)

    @PostMapping
    fun createGroup(@RequestBody @Valid body: CreateGroupDto, principal: UsernamePasswordAuthenticationToken): GroupResponseDto = mapper.toDto(createGroupUseCase.execute(body.users, principal.principal as User, body.name))

    @GetMapping("/{id}")
    fun getGroup(principal: UsernamePasswordAuthenticationToken, @PathVariable id: UUID): GroupResponseDto =
            mapper.toDto(findingGroupUseCase.execute(id, principal.principal as User))

    @PutMapping("/{id}")
    fun putMemberInGroup(@RequestBody @Valid body: AddUserToGroupDto, principal: UsernamePasswordAuthenticationToken, @PathVariable id: UUID): GroupResponseDto =
            mapper.toDto(addUserToGroupUseCase.execute(principal.principal as User, id, body.users))
}