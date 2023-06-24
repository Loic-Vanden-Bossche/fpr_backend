package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Message
import com.esgi.domainmodels.User
import java.util.UUID

interface MessagesPersistence {

    fun findAllInGroup(group: Group, page: Int = 0, size: Int = 20): List<Message>

    fun findById(message: UUID): Message?

    fun writeMessageToGroup(from: User, group: Group, message: String): Message

    fun editMessage(message: Message, text: String): Message
}