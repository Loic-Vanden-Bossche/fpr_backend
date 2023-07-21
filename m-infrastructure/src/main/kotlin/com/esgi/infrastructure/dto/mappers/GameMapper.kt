package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.Game
import com.esgi.infrastructure.dto.output.GameResponseDto
import com.esgi.infrastructure.persistence.entities.GameEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GameMapper {
    fun toDomain(game: GameEntity): Game
    fun toEntity(game: Game): GameEntity
    fun toDto(game: Game): GameResponseDto
}