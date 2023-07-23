package com.esgi.infrastructure.persistence.repositories

import com.esgi.infrastructure.persistence.entities.GroupEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GroupsRepository : CrudRepository<GroupEntity, UUID>