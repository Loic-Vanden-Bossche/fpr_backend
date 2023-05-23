package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.output.UserResponseDto
import com.esgi.infrastructure.persistence.entities.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

//import org.mapstruct.Mapping

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {

//    @Mapping(source = "createdAt", target = "createdOn")
    fun toDomain(user: UserEntity): User

//    @Mapping(source = "createdOn", target = "createdAt")
    fun toEntity(email: String, nickname: String, password: String): UserEntity

    fun toDto(user: User): UserResponseDto
}