package com.esgi.infrastructure.controllers

import com.esgi.*
import com.esgi.applicationservices.FindingAllUsersUseCase
import com.esgi.domainmodels.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("users")
class UsersController(
    private val findingAllUsersUseCase: FindingAllUsersUseCase,
) {
    @GetMapping("")
    @ResponseBody
    fun getUsers(): List<User> {
        return findingAllUsersUseCase.execute()
    }
}