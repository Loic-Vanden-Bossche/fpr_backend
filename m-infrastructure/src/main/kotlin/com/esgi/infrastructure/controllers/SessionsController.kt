package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.sessions.StartingSessionUseCase
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sessions")
@SecurityRequirement(name = "fpr")
class SessionsController(
    private val startingSessionUseCase: StartingSessionUseCase,
) {
    @GetMapping
    @ResponseBody
    fun startSession() {
        startingSessionUseCase.execute()
    }
}

