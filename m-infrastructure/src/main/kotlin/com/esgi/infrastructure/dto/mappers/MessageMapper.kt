package com.esgi.infrastructure.dto.mappers

import com.esgi.domainmodels.Message
import com.esgi.infrastructure.dto.output.MessageResponseDto
import com.esgi.infrastructure.dto.output.MessageResponseType
import com.esgi.infrastructure.persistence.entities.MessageEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface MessageMapper {
    fun toDto(group: Message, type: MessageResponseType): MessageResponseDto

    fun toDomain(entity: MessageEntity): Message
}