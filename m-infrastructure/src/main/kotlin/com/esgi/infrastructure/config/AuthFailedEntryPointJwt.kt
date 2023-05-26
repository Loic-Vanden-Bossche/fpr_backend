package com.esgi.infrastructure.config


import com.esgi.infrastructure.common.ErrorResponse
import com.esgi.infrastructure.services.DevToolsChecker
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthFailedEntryPointJwt(
    private val devToolsChecker: DevToolsChecker,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val httpStatus = HttpStatus.UNAUTHORIZED

        val errorResponse = ErrorResponse.fromException(
            authException,
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