package com.esgi.infrastructure.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SocketHandler: WebSocketHandler {
    val sessions: MutableList<IdSession> = CopyOnWriteArrayList()

    val mapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(IdSession("", session))
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        val messageData = mapper.readValue<SignalType>(message.payload as String)
        when(messageData.type){
            "identify" -> {
                val data = mapper.readValue<SignalMessage<IdentityMessage>>(message.payload as String)
                sessions.find { it.session.id == session.id }?.let { it.id = data.data.me }
            }
            "call-group" -> {
                val data = mapper.readValue<SignalMessage<CallGroupMessage>>(message.payload as String)
                sessions.find { it.id == data.data.to }?.session?.sendMessage(
                    TextMessage(mapper.writeValueAsString(SignalMessage(
                        "new-user",
                        NewUserMessage(sessions.find { it.session.id == session.id }!!.id, data.data.offer)
                    )))
                )
            }
            "make-answer" -> {
                val data = mapper.readValue<SignalMessage<MakeAnswerMessage>>(message.payload as String)
                sessions.find { it.id == data.data.to }?.session?.sendMessage(
                    TextMessage(mapper.writeValueAsString(SignalMessage(
                        "new-answer",
                        NewAnswerMessage(data.data.answer, sessions.find { it.session.id == session.id }!!.id)
                    )))
                )
            }
            else -> null
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println(exception)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        sessions.removeIf { it.session.id == session.id }
    }

    override fun supportsPartialMessages(): Boolean {
        return true
    }
}

data class IdSession(
    var id: String,
    val session: WebSocketSession
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SignalType(
    val type: String
)


data class SignalMessage<T> @JsonCreator constructor(
    val type: String,
    val data: T
)

data class IdentityMessage(
    val me: String
)

data class CallGroupMessage(
    val to: String,
    val offer: OfferData
)

data class NewUserMessage(
    val from: String,
    val offer: OfferData
)

data class OfferData(
    val sdp: String,
    val type: String
)

data class MakeAnswerMessage(
    val answer: OfferData,
    val to: String
)

data class NewAnswerMessage(
    val answer: OfferData,
    val from: String
)