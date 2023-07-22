package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.GroupsPersistence
import com.esgi.domainmodels.Group
import com.esgi.domainmodels.GroupType
import com.esgi.domainmodels.User
import com.esgi.infrastructure.dto.mappers.GroupMapper
import com.esgi.infrastructure.persistence.entities.GroupEntity
import com.esgi.infrastructure.persistence.entities.UserGroupEntity
import com.esgi.infrastructure.persistence.repositories.*
import jakarta.transaction.Transactional
import org.mapstruct.factory.Mappers
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class GroupPersistenceAdapter(
    private val groupsRepository: GroupsRepository,
    private val usersRepository: UsersRepository,
    private val usersGroupsRepository: UsersGroupsRepository,
    private val friendsRepository: FriendsRepository,
    private val messagesRepository: MessagesRepository
): GroupsPersistence {
    private val mapper = Mappers.getMapper(GroupMapper::class.java)

    override fun findAll(user: User): List<Group> = usersRepository.findUserGroups(user.id).map{ mapper.toDomain(it.group) }.sortedByDescending { it.members.find { it.user.id == user.id }!!.lastRead }

    override fun find(id: UUID): Group? = groupsRepository.findByIdOrNull(id)?.let { mapper.toDomain(it) }

    override fun create(users: List<User>, name: String): Group {
        val group = GroupEntity().apply {
            this.name = name
            type = GroupType.GROUP
            this.users = users.map {
                val user = usersRepository.findByIdOrNull(it.id)!!
                UserGroupEntity(user, this, Date.from(Instant.now()))
            }.toMutableList()
        }
        groupsRepository.save(group)
        group.users.forEach {
            usersGroupsRepository.save(it)
        }
        return mapper.toDomain(group)
    }

    override fun createFriendGroup(user1: User, user2: User): Group {
        val group = GroupEntity().apply {
            this.name = "${user1.nickname} ${user2.nickname}"
            type = GroupType.FRIEND
            this.users = listOf(user1, user2).map {
                val user = usersRepository.findByIdOrNull(it.id)!!
                UserGroupEntity(user, this, Date.from(Instant.now()))
            }.toMutableList()
        }
        groupsRepository.save(group)
        group.users.forEach {
            usersGroupsRepository.save(it)
        }
        return mapper.toDomain(group)
    }

    override fun updateGroupName(group: Group, name: String): Group {
        val entity = groupsRepository.findByIdOrNull(group.id)!!
        entity.name = name
        groupsRepository.save(entity)
        return mapper.toDomain(entity)
    }

    override fun addUser(group: Group, users: List<User>): Group {
        val groupEntity = groupsRepository.findByIdOrNull(group.id)!!
        val usersEntity = users.map {
            val user = usersRepository.findByIdOrNull(it.id)!!
            UserGroupEntity(user, groupEntity, Date.from(Instant.now()))
        }
        groupEntity.users.addAll(usersEntity)
        groupsRepository.save(groupEntity)
        return mapper.toDomain(groupEntity)
    }

    override fun removeUserFromGroup(user: User, group: Group): Group? {
        val groupEntity = groupsRepository.findByIdOrNull(group.id)!!
        return if(groupEntity.users.size == 1){
            groupsRepository.delete(groupEntity)
            null
        }else{
            groupEntity.users.removeIf{ it.user.id == user.id }
            groupsRepository.save(groupEntity)
            mapper.toDomain(groupEntity)
        }
    }

    override fun updateRead(user: User, group: Group) {
        val groupEntity = groupsRepository.findByIdOrNull(group.id)!!
        val userEntity = usersRepository.findByIdOrNull(user.id)!!
        val ug = usersGroupsRepository.findByGroupAndUser(groupEntity, userEntity)
        ug.lastRead = Date.from(Instant.now())
        usersGroupsRepository.save(ug)
    }

    @Transactional
    override fun deleteFriendGroup(user: User, friend: User) {
        val friendEntity = friendsRepository.getDeniedFromFor(user.id, friend.id)!!
        val group = friendEntity.group!!
        friendEntity.group = null
        friendsRepository.save(friendEntity)
        usersGroupsRepository.deleteAllByGroup(group)
        messagesRepository.deleteAllByGroup(group)
        groupsRepository.deleteById(group.id!!)
    }
}