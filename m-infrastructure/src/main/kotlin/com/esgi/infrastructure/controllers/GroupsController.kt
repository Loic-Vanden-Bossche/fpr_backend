package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.groups.FindingAllGroupsUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.dto.output.GroupResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/groups")
@SecurityRequirement(name = "fpr")
class GroupsController(
    private val findingAllGroupsUseCase: FindingAllGroupsUseCase
) {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)

    @GetMapping
    @ResponseBody
    fun getGroups(principal: UsernamePasswordAuthenticationToken): List<GroupResponseDto> = findingAllGroupsUseCase.execute((principal.principal as User)).map(mapper::toDto)
}