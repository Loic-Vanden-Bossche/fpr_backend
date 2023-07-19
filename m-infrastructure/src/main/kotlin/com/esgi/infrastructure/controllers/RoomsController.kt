package com.esgi.infrastructure.controllers

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rooms")
@SecurityRequirement(name = "fpr")
class RoomsController() {
}

