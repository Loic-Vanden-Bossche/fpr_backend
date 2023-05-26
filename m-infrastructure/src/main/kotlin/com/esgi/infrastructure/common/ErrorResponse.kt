package com.esgi.infrastructure.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus

data class ErrorResponse(
    val status: Int,
    val error: String,
    val stack: String?,
    val message: String,
    val path: String,
) {
    companion object {
        fun fromException(
            ex: Exception, status: HttpStatus, req: HttpServletRequest, includeStackTrace: Boolean = false
        ): ErrorResponse {
            return ErrorResponse(
                status.value(),
                status.reasonPhrase,
                if (includeStackTrace) ex.stackTraceToString() else null,
                ex.message!!,
                req.servletPath,
            )
        }
    }
}