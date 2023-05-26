package com.esgi.infrastructure.common

import com.esgi.domainmodels.exceptions.EmailAlreadyExistsException
import com.esgi.domainmodels.exceptions.LoginFailedException
import com.esgi.domainmodels.exceptions.NotFoundException
import com.esgi.infrastructure.services.DevToolsChecker
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler(
    val devToolsChecker: DevToolsChecker,
) : ResponseEntityExceptionHandler() {

    private fun handleDomainException(
        ex: Exception,
        req: HttpServletRequest,
        status: HttpStatus
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse.fromException(
                ex,
                status,
                req,
                devToolsChecker.isDevToolsPresent()
            ),
            status
        )
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(
        ex: EmailAlreadyExistsException,
        req: HttpServletRequest
    ): ResponseEntity<ErrorResponse> { return handleDomainException(ex, req, HttpStatus.CONFLICT) }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        ex: NotFoundException,
        req: HttpServletRequest
    ): ResponseEntity<ErrorResponse> { return handleDomainException(ex, req, HttpStatus.NOT_FOUND) }

    @ExceptionHandler(LoginFailedException::class)
    fun handleInvalidBearerTokenException(
        ex: LoginFailedException,
        req: HttpServletRequest
    ): ResponseEntity<ErrorResponse> { return handleDomainException(ex, req, HttpStatus.UNAUTHORIZED) }
}