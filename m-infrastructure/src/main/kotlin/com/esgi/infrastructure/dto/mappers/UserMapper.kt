package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.output.UserResponseDto
import com.esgi.infrastructure.persistence.entities.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {
    fun toDomain(user: UserEntity): User
    fun toDto(user: User): UserResponseDto
    fun toEntity(user: User): UserEntity
}