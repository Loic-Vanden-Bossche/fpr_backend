package com.esgi.infrastructure.persistence.adapters

import com.esgi.applicationservices.persistence.RoomsPersistence
import com.esgi.domainmodels.Room
import com.esgi.infrastructure.dto.mappers.RoomMapper
import com.esgi.infrastructure.persistence.repositories.RoomsRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component
import java.util.*

@Component
class RoomsRepositoryAdapter(
        private val roomsRepository: RoomsRepository
): RoomsPersistence {
    private val mapper = Mappers.getMapper(RoomMapper::class.java)

    override fun findById(roomId: String): Room? {
        val roomEntity = roomsRepository.findById(UUID.fromString(roomId)).orElse(null) ?: return null

        return mapper.toDomain(roomEntity)
    }
}