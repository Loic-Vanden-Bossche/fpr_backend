package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.persistence.repositories.GroupsRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import java.util.*

class GroupPersistenceAdapter(
    private val groupsRepository: GroupsRepository,
    private val usersRepository: UsersRepository
): GroupsPersistence {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)

    override fun findAll(user: User): List<Group> = usersRepository.findUserGroups(UUID.fromString(user.id)).map(mapper::toDomain)
}