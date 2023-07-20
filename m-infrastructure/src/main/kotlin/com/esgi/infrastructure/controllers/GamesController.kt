package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.games.BuildGameUseCase
import com.esgi.applicationservices.usecases.games.CreateGameUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CreateGameDto
import com.esgi.infrastructure.dto.mappers.GameMapper
import com.esgi.infrastructure.dto.output.GameResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.mapstruct.factory.Mappers
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/games")
@SecurityRequirement(name = "fpr")
class GamesController(
    private val buildGameUseCase: BuildGameUseCase,
    private val createGameUseCase: CreateGameUseCase
) {
    private val mapper = Mappers.getMapper(GameMapper::class.java)

    @PostMapping(path  = ["build"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun build(file: MultipartFile) {
        buildGameUseCase("448a82c3-d29c-4921-8b45-480ae59a7cf1", file.inputStream)
    }

    @PostMapping(path  = ["create"])
    @ResponseBody
    fun createGame(@RequestBody @Valid body: CreateGameDto, principal: UsernamePasswordAuthenticationToken): GameResponseDto {
        return mapper.toDto(createGameUseCase(body.title, principal.principal as User))
    }
}