package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Room
import com.esgi.domainmodels.RoomInvitationStatus
import com.esgi.infrastructure.dto.output.RoomResponseDto
import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.RoomEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RoomMapper {
    fun toDomain(user: RoomEntity, invitationStatus: RoomInvitationStatus?): Room

    fun toDto(room: Room): RoomResponseDto

    @Mapping(target = "members", source = "users")
    fun toDomainGroup(group: GroupEntity): Group
}