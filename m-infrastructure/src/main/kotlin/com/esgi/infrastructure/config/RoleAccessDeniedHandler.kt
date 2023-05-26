package com.esgi.infrastructure.config

import com.esgi.infrastructure.common.ErrorResponse
import com.esgi.infrastructure.services.DevToolsChecker
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class RoleAccessDeniedHandler(
    private val devToolsChecker: DevToolsChecker,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val httpStatus = HttpStatus.FORBIDDEN

        val errorResponse = ErrorResponse.fromException(
            accessDeniedException,
            httpStatus,
            request,
            devToolsChecker.isDevToolsPresent()
        )

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = httpStatus.value()

        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, errorResponse)
    }
}