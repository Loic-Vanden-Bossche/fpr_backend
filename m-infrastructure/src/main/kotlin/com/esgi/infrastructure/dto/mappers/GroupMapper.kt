package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.Group
import com.esgi.infrastructure.dto.output.GroupResponseDto
import com.esgi.infrastructure.persistence.entities.GroupEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GroupMapper {
    fun toDto(group: Group): GroupResponseDto

    @Mapping(target = "members", source = "users")
    fun toDomain(entity: GroupEntity): Group
}