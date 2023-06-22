package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.GroupType
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.repositories.GroupsRepository
import com.esgi.infrastructure.persistence.repositories.UsersRepository
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GroupPersistenceAdapter(
    private val groupsRepository: GroupsRepository,
    private val usersRepository: UsersRepository
): GroupsPersistence {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)

    override fun findAll(user: User): List<Group> = usersRepository.findUserGroups(UUID.fromString(user.id)).map(mapper::toDomain)

    override fun create(users: List<User>, name: String): Group {
        val group = GroupEntity().apply {
            this.name = name
            type = GroupType.GROUP
            this.users = users.map{ usersRepository.findByIdOrNull(UUID.fromString(it.id))!! }
        }
        groupsRepository.save(group)
        return mapper.toDomain(group)
    }
}