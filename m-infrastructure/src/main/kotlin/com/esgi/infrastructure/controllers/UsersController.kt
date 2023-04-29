package com.esgi.infrastructure.controllers

import com.esgi.*
import com.esgi.applicationservices.FindingAllUsersUseCase
import com.esgi.domainmodels.User
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "fpr")
class UsersController(
    private val findingAllUsersUseCase: FindingAllUsersUseCase,
) {
    @GetMapping("")
    @ResponseBody
    fun getUsers(): List<User> {
        return findingAllUsersUseCase.execute()
    }
}