package com.esgi.infrastructure.services

import com.esgi.applicationservices.usecases.groups.FindingGroupUseCase
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.input.CallGroupMessageDto
import com.esgi.infrastructure.dto.input.ConnectGroupMessageDto
import com.esgi.infrastructure.dto.input.MakeAnswerMessageDto
import com.esgi.infrastructure.dto.output.*
import javassist.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class WebRTCSignalService(
    private val findingGroupUseCase: FindingGroupUseCase,
    private val tokenService: TokensService
) {
    val sessions: MutableMap<WebSocketSession, Pair<User, Group?>> = mutableMapOf()

    fun handleIdentify(jwt: String, session: WebSocketSession): IdentifiedMessageResponseDto = IdentifiedMessageResponseDto(
        tokenService.parseToken(jwt)?.let {
            if(it in sessions.values.map { u -> u.first }){
                false
            }else{
                sessions[session] = it to null
                true
            }
        } ?: false
    )

    fun handleCallGroup(session: WebSocketSession, callGroupMessageDto: CallGroupMessageDto): Pair<NewUserMessageResponseDto, WebSocketSession>? {
        val userGroup = sessions[session] ?: return null
        try {
            val group = findingGroupUseCase.execute(callGroupMessageDto.group, userGroup.first)
            val to = group.members.find { it.user.id == callGroupMessageDto.to } ?: return null
            val sessionTo = sessions.filter { it.value.first.id == to.user.id }.keys.first()
            return NewUserMessageResponseDto(to.user.id, callGroupMessageDto.offer) to sessionTo
        }catch (_: NotFoundException){
            return null
        }
    }

    fun handleResponse(session: WebSocketSession, makeAnswerMessageDto: MakeAnswerMessageDto): Pair<NewAnswerMessageResponseDto, WebSocketSession>?{
        val userGroup = sessions[session] ?: return null
        try {
            val group = findingGroupUseCase.execute(makeAnswerMessageDto.group, userGroup.first)
            val to = group.members.find { it.user.id == makeAnswerMessageDto.to } ?: return null
            val sessionTo = sessions.filter { it.value.first.id == to.user.id }.keys.first()
            return NewAnswerMessageResponseDto(makeAnswerMessageDto.answer, to.user.id) to sessionTo
        }catch (_: NotFoundException){
            return null
        }
    }

    fun handleConnect(session: WebSocketSession, connectGroupMessageDto: ConnectGroupMessageDto): ConnectedStatusMessageResponseDto {
        val userGroup = sessions[session] ?: return ConnectedStatusMessageResponseDto(false)
        return ConnectedStatusMessageResponseDto( try {
            val group = findingGroupUseCase.execute(connectGroupMessageDto.group, userGroup.first)
            sessions[session] = userGroup.first to group
            true
        }catch (_: NotFoundException){
            false
        })
    }

    fun handlePresent(session: WebSocketSession): PresentMessageResponseDto? {
        val userGroup = sessions[session] ?: return null
        userGroup.second ?: return null
        val users = sessions.filter { it.value.second?.id == userGroup.second?.id && it.value.first.id != userGroup.first.id }.map { it.value.first.id }
        return PresentMessageResponseDto(users)
    }

    fun handleDisconnect(session: WebSocketSession) {
        sessions.remove(session)
    }
}