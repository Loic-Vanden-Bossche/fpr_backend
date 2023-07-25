package com.esgi.infrastructure.controllers

import com.esgi.applicationservices.usecases.games.*
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CreateGameDto
import com.esgi.infrastructure.dto.input.SetGameVisibilityDto
import com.esgi.infrastructure.dto.input.SetNeedSeedDto
import com.esgi.infrastructure.dto.mappers.GameMapper
import com.esgi.infrastructure.dto.output.GameResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
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
    private val createGameUseCase: CreateGameUseCase,
    private val findingAllGamesUseCase: FindingAllGamesUseCase,
    private val findingMyGamesUseCase: FindingMyGamesUseCase,
    private val addGamePictureUseCase: AddGamePictureUseCase,
    private val setGameVisibilityUseCase: SetGameVisibilityUseCase,
    private val setNeedSeedUseCase: SetNeedSeedUseCase,
    private val deleteGameUseCase: DeleteGameUseCase
) {
    private val mapper = Mappers.getMapper(GameMapper::class.java)

    @GetMapping
    fun findAll(): List<GameResponseDto> {
        return findingAllGamesUseCase().map { mapper.toDto(it) }
    }

    @GetMapping("self")
    fun findSelf(
        principal: UsernamePasswordAuthenticationToken
    ): List<GameResponseDto> {
        return findingMyGamesUseCase((principal.principal as User).id).map { mapper.toDto(it) }
    }

    @PostMapping("{id}/build", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun build(
        principal: UsernamePasswordAuthenticationToken,
        file: MultipartFile?,
        language: String?,
        @PathVariable @NotNull id: String
    ): GameResponseDto {
        return mapper.toDto(
            buildGameUseCase((principal.principal as User).id, id, file?.inputStream, file?.contentType, language)
        )
    }

    @PostMapping("{id}/picture", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun addPicture(
        principal: UsernamePasswordAuthenticationToken,
        @RequestPart("file") file: MultipartFile,
        @PathVariable @NotNull id: String
    ): GameResponseDto = mapper.toDto(
        addGamePictureUseCase(
            (principal.principal as User).id,
            id,
            file.inputStream,
            file.contentType
        )
    )

    @PatchMapping("{id}/visibility")
    @ResponseBody
    fun setVisibility(
        principal: UsernamePasswordAuthenticationToken,
        @RequestBody @NotNull body: SetGameVisibilityDto,
        @PathVariable @NotNull id: String
    ): GameResponseDto {
        return mapper.toDto(
            setGameVisibilityUseCase(
                (principal.principal as User).id,
                id,
                body.public
            )
        )
    }

    @PatchMapping("/{id}/needSeed")
    fun setNeedSeed(
        principal: UsernamePasswordAuthenticationToken,
        @RequestBody @NotNull body: SetNeedSeedDto,
        @PathVariable @NotNull id: String
    ): GameResponseDto {
        return mapper.toDto(
            setNeedSeedUseCase(
                (principal.principal as User).id,
                id,
                body.needSeed
            )
        )
    }

    @PostMapping("create")
    @ResponseBody
    fun createGame(
        principal: UsernamePasswordAuthenticationToken,
        @RequestBody @Valid body: CreateGameDto
    ): GameResponseDto {
        return mapper.toDto(
            createGameUseCase(
                body.title,
                body.nbMinPlayers,
                body.nbMaxPlayers,
                body.isDeterministic,
                principal.principal as User
            )
        )
    }

    @DeleteMapping("{id}")
    fun deleteGame(
        principal: UsernamePasswordAuthenticationToken,
        @PathVariable @NotNull id: String
    ) {
        deleteGameUseCase((principal.principal as User).id, id)
    }
}