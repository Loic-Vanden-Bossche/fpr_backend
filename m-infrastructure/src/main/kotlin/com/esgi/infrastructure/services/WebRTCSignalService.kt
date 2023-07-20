package com.esgi.infrastructure.services

import com.esgi.applicationservices.usecases.groups.FindingGroupUseCase
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CallGroupMessageDto
import com.esgi.infrastructure.dto.input.MakeAnswerMessageDto
import com.esgi.infrastructure.dto.output.NewUserMessageResponseDto
import com.esgi.infrastructure.dto.output.IdentifiedMessageResponseDto
import com.esgi.infrastructure.dto.output.NewAnswerMessageResponseDto
import javassist.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class WebRTCSignalService(
    private val findingGroupUseCase: FindingGroupUseCase,
    private val tokenService: TokensService
) {
    val sessions: MutableMap<WebSocketSession, User> = mutableMapOf()

    fun handleIdentify(jwt: String, session: WebSocketSession): IdentifiedMessageResponseDto = IdentifiedMessageResponseDto(
        tokenService.parseToken(jwt)?.let {
            if(it in sessions.values){
                false
            }else{
                sessions[session] = it
                true
            }
        } ?: false
    )

    fun handleCallGroup(session: WebSocketSession, callGroupMessageDto: CallGroupMessageDto): Pair<NewUserMessageResponseDto, WebSocketSession>? {
        val user = sessions[session] ?: return null
        try {
            val group = findingGroupUseCase.execute(callGroupMessageDto.group, user)
            val to = group.members.find { it.user.id == callGroupMessageDto.to } ?: return null
            val sessionTo = sessions.filter { it.value.id == to.user.id }.keys.first()
            return NewUserMessageResponseDto(to.user.id, callGroupMessageDto.offer) to sessionTo
        }catch (_: NotFoundException){
            return null
        }
    }

    fun handleResponse(session: WebSocketSession, makeAnswerMessageDto: MakeAnswerMessageDto): Pair<NewAnswerMessageResponseDto, WebSocketSession>?{
        val user = sessions[session] ?: return null
        try {
            val group = findingGroupUseCase.execute(makeAnswerMessageDto.group, user)
            val to = group.members.find { it.user.id == makeAnswerMessageDto.to } ?: return null
            val sessionTo = sessions.filter { it.value.id == to.user.id }.keys.first()
            return NewAnswerMessageResponseDto(makeAnswerMessageDto.answer, to.user.id) to sessionTo
        }catch (_: NotFoundException){
            return null
        }
    }

    fun handleDisconnect(session: WebSocketSession) {
        sessions.remove(session)
    }
}