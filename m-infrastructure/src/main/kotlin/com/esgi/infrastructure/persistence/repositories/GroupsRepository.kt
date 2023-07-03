package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface GroupsRepository: CrudRepository<GroupEntity, UUID>