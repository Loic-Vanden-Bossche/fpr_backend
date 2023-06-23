package com.esgi.applicationservices.persistence

import com.esgi.domainmodels.Group
import com.esgi.domainmodels.Message

interface MessagesPersistence {

    fun findAllInGroup(group: Group): List<Message>
}