package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.games.BuildGameUseCase
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/games")
@SecurityRequirement(name = "fpr")
class GamesController(
    private val buildGameServiceProd: BuildGameUseCase
) {
    @PostMapping(path  = ["build"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun build(@RequestPart("file") file: MultipartFile) {
        buildGameServiceProd.execute("448a82c3-d29c-4921-8b45-480ae59a7cf1", file.inputStream)
    }
}