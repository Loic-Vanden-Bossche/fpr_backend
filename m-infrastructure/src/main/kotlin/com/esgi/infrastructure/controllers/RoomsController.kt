package com.esgi.infrastructure.controllers

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rooms")
@SecurityRequirement(name = "fpr")
class RoomsController

