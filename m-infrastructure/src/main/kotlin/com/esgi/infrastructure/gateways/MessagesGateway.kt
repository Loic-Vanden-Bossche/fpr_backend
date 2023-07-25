package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.usecases.groups.message.DeleteMessageInGroupUseCase
import com.esgi.applicationservices.usecases.groups.message.EditMessageInGroupUseCase
import com.esgi.applicationservices.usecases.groups.message.WriteMessageToGroupUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.DeleteMessageDto
import com.esgi.infrastructure.dto.input.EditMessageDto
import com.esgi.infrastructure.dto.input.WriteMessageDto
import com.esgi.infrastructure.dto.mappers.MessageMapper
import com.esgi.infrastructure.dto.output.MessageResponseTypeDto
import jakarta.inject.Inject
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import java.util.*

@Controller
@CrossOrigin(origins = ["http://localhost:1234", "https://jxy.me"], allowCredentials = "true")
class MessagesGateway(
    @Autowired
    private val template: SimpMessagingTemplate,
    @Inject
    private val writeMessageToGroupUseCase: WriteMessageToGroupUseCase,
    @Inject
    private val editMessageInGroupUseCase: EditMessageInGroupUseCase,
    @Inject
    private val deleteMessageInGroupUseCase: DeleteMessageInGroupUseCase
) {
    private val mapper = Mappers.getMapper(MessageMapper::class.java)

    @MessageMapping("/{id}/messages")
    fun writeToGroup(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable id: UUID,
        message: WriteMessageDto
    ) {
        val result = writeMessageToGroupUseCase.execute(principal.principal as User, id, message.message)
        template.convertAndSend("/groups/$id/messages", mapper.toDto(result, MessageResponseTypeDto.NEW))
    }

    @MessageMapping("/{id}/messages/edit")
    fun editMessageInGroup(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable id: UUID,
        message: EditMessageDto
    ) {
        val result = editMessageInGroupUseCase(principal.principal as User, id, message.id, message.message)
        template.convertAndSend("/groups/$id/messages", mapper.toDto(result, MessageResponseTypeDto.EDIT))
    }

    @MessageMapping("/{id}/messages/delete")
    fun deleteMessageInGroup(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable id: UUID,
        message: DeleteMessageDto
    ) {
        val result = deleteMessageInGroupUseCase(principal.principal as User, id, message.id)
        template.convertAndSend("/groups/$id/messages", mapper.toDto(result, MessageResponseTypeDto.DELETE))
    }
}