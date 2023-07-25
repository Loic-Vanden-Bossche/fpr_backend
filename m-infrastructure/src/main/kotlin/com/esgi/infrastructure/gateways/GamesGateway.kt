package com.esgi.infrastructure.gateways

import com.esgi.applicationservices.usecases.rooms.*
import com.esgi.domainmodels.User
import com.esgi.domainmodels.exceptions.BadRequestException
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.dto.input.CreateRoomDto
import com.esgi.infrastructure.dto.input.RollbackRoomDto
import com.esgi.infrastructure.dto.output.games.*
import com.esgi.infrastructure.services.SessionsService
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin

@Controller
@CrossOrigin(origins = ["https://jxy.me"], allowCredentials = "true")
class GamesGateway(
    private val createRoomUseCase: CreateRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val startSessionUseCase: StartSessionUseCase,
    private val playSessionActionUseCase: PlaySessionActionUseCase,
    private val findRoomUseCase: FindRoomUseCase,
    private val sessionsService: SessionsService,
) {
    val jsonMapper = jacksonObjectMapper()

    @MessageMapping("/createRoom")
    @SendTo("/rooms/created")
    fun createRoom(principal: UsernamePasswordAuthenticationToken, roomData: CreateRoomDto): CreateRoomResponseDto {
        println("Creating room with game ${roomData.gameId}")

        val room = try {
            createRoomUseCase(
                roomData.gameId,
                roomData.groupId,
                principal.principal as User,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return CreateRoomResponseDto(false, reason = "Error creating room")
        }

        val roomId = room.id

        return try {
            CreateRoomResponseDto(
                true,
                roomId,
                sessionsService.createSessionAndListen(roomData.gameId, roomId.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
            CreateRoomResponseDto(false, reason = e.message)
        }
    }

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun joinRoom(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String
    ): JoinRoomResponseDto {
        println("Joining room $roomId")

        if (sessionsService.isSessionMissing(roomId)) {
            return JoinRoomResponseDto(false, reason = "Session not found")
        }

        val userId = (principal.principal as User).id

        return try {
            joinRoomUseCase(roomId, userId.toString())
            JoinRoomResponseDto(true, userId)
        } catch (e: IllegalStateException) {
            sessionsService.sendLatestStateToUser(roomId, userId)

            JoinRoomResponseDto(true)
        } catch (e: BadRequestException) {
            JoinRoomResponseDto(false, reason = e.message)
        } catch (e: NotFoundException) {
            JoinRoomResponseDto(false, reason = e.message)
        }
    }

    @MessageMapping("/startGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun startGame(@DestinationVariable roomId: String): StartedGameResponseDto {
        if (sessionsService.isSessionMissing(roomId)) {
            return StartedGameResponseDto(false, reason = "Session not found")
        }

        val room = findRoomUseCase(roomId) ?: return StartedGameResponseDto(false, "Room not found")

        return try {
            sessionsService.startGameSession(
                room,
                startSessionUseCase(roomId).players.size
            )
            StartedGameResponseDto(true)
        } catch (e: IllegalStateException) {
            StartedGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            StartedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/rollbackGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun rollbackGame(@DestinationVariable roomId: String, roomData: RollbackRoomDto): RollbackedGameResponseDto {
        println("Resuming game $roomId")
        if (sessionsService.isSessionMissing(roomId)) {
            return RollbackedGameResponseDto(false, reason = "Session not found")
        }

        val room = findRoomUseCase(roomId) ?: return RollbackedGameResponseDto(false, "Room not found")

        println("Room $roomId found")

        println("Rolling back game $roomId to action ${roomData.actionId}")

        return try {
            sessionsService.rollbackSession(
                room,
                roomData.actionId
            )

            RollbackedGameResponseDto(true)
        } catch (e: BadRequestException) {
            RollbackedGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            RollbackedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/resumeGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun resumeGame(@DestinationVariable roomId: String): ResumedGameResponseDto {
        println("Resuming game $roomId")
        if (sessionsService.isSessionExisting(roomId)) {
            return ResumedGameResponseDto(false, reason = "Session is already running")
        }

        val room = findRoomUseCase(roomId) ?: return ResumedGameResponseDto(false, "Room not found")

        println("Room $roomId found")

        return try {
            sessionsService.resumeSession(
                room,
            )

            ResumedGameResponseDto(true)
        } catch (e: BadRequestException) {
            ResumedGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            ResumedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/stopGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun stopGame(@DestinationVariable roomId: String): StoppedGameResponseDto {
        if (sessionsService.isSessionMissing(roomId)) {
            return StoppedGameResponseDto(false, reason = "Session not found")
        }

        return try {
            sessionsService.stopGameSession(roomId, emptyList())
            StoppedGameResponseDto(true)
        } catch (e: NotFoundException) {
            StoppedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/pauseGame/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun pauseGame(@DestinationVariable roomId: String): PausedGameResponseDto {
        if (sessionsService.isSessionMissing(roomId)) {
            return PausedGameResponseDto(false, reason = "Session not found")
        }

        return try {
            sessionsService.pauseGameSession(roomId)
            PausedGameResponseDto(true)
        } catch (e: NotFoundException) {
            PausedGameResponseDto(false, e.message)
        } catch (e: BadRequestException) {
            PausedGameResponseDto(false, e.message)
        }
    }

    @MessageMapping("/play/{roomId}")
    @SendTo("/rooms/{roomId}")
    fun play(
        principal: UsernamePasswordAuthenticationToken,
        @DestinationVariable roomId: String,
        instruction: String
    ): PlayGameResponseDto {
        if (sessionsService.isSessionMissing(roomId)) {
            return PlayGameResponseDto(false, reason = "Session not found")
        }

        val room = findRoomUseCase(roomId) ?: return PlayGameResponseDto(false, "Room not found")

        val player = room.players.find { it.user.id == (principal.principal as User).id }
            ?: return PlayGameResponseDto(false, "You are not in this room")

        val parsedInstruction = try {
            val tree = jsonMapper.readTree(instruction)

            if (tree["actions"].isArray) {
                tree["actions"].forEach {
                    if (it.isObject) {
                        (it as ObjectNode).put("player", player.playerIndex)
                    }
                }
            }

            tree
        } catch (_: Exception) {
            PlayGameResponseDto(false, "Invalid JSON instruction")
        }
        val completedInstruction = sessionsService.prepareGameAction(
            roomId,
            jsonMapper.writeValueAsString(parsedInstruction),
            player.user.id.toString()
        )

        if (completedInstruction.isNullOrEmpty()) {
            return PlayGameResponseDto(true, "Played instruction - waiting for other players")
        }

        return try {
            playSessionActionUseCase(
                roomId,
                (principal.principal as User).id.toString(),
                completedInstruction
            )

            println("Sending instruction $completedInstruction to room $roomId")

            sessionsService.playGameAction(roomId, completedInstruction)

            PlayGameResponseDto(true)
        } catch (e: BadRequestException) {
            PlayGameResponseDto(false, e.message)
        } catch (e: NotFoundException) {
            PlayGameResponseDto(false, e.message)
        }
    }
}