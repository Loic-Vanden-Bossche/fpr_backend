package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomInvitationStatus
import com.esgi.infrastructure.dto.output.RoomResponseDto
import com.esgi.infrastructure.persistence.entities.RoomEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RoomMapper {
    fun toDomain(user: RoomEntity, invitationStatus: RoomInvitationStatus?): Room
    fun toEntity(user: Room): RoomEntity
    fun toDto(user: Room): RoomResponseDto
}