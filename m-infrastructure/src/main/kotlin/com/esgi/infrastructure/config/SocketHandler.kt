package com.esgi.infrastructure.config

import com.esgi.infrastructure.dto.SignalMessage
import com.esgi.infrastructure.dto.SignalType
import com.esgi.infrastructure.dto.input.CallGroupMessageDto
import com.esgi.infrastructure.dto.input.IdentityMessageDto
import com.esgi.infrastructure.dto.input.MakeAnswerMessageDto
import com.esgi.infrastructure.services.WebRTCSignalService
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SocketHandler(
    private val webRTCSignalService: WebRTCSignalService
): WebSocketHandler {
    val mapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {

    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val messageData = mapper.readValue<SignalType>(message.payload as String)
        when(messageData.type){
            "identify" -> {
                val data = mapper.readValue<SignalMessage<IdentityMessageDto>>(message.payload as String)
                session.sendMessage(
                    TextMessage(
                        mapper.writeValueAsString(
                            SignalMessage(
                                "identified",
                                webRTCSignalService.handleIdentify(
                                    data.data.jwt,
                                    session
                                )
                            )
                        )
                    )
                )
            }
            "call-group" -> {
                val data = mapper.readValue<SignalMessage<CallGroupMessageDto>>(message.payload as String)
                val resp = webRTCSignalService.handleCallGroup(session, data.data) ?: return
                resp.second.sendMessage(TextMessage(
                    mapper.writeValueAsString(
                        SignalMessage("new-user", resp.first)
                    )
                ))
            }
            "make-answer" -> {
                val data = mapper.readValue<SignalMessage<MakeAnswerMessageDto>>(message.payload as String)
                val resp = webRTCSignalService.handleResponse(session, data.data) ?: return
                resp.second.sendMessage(TextMessage(
                    mapper.writeValueAsString(
                        SignalMessage("new-answer", resp.first)
                    )
                ))
            }
            else -> null
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println(exception)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        webRTCSignalService.handleDisconnect(session)
    }

    override fun supportsPartialMessages(): Boolean {
        return true
    }
}