package com.esgi.infrastructure

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.socket.config.annotation.EnableWebSocket

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "FPR API", version = "1.0", description = "FPR API"))
@SecurityScheme(name = "fpr", type = SecuritySchemeType.HTTP, `in` = SecuritySchemeIn.HEADER, scheme = "Bearer")
@EnableWebSocket
class FprApplication

fun main(args: Array<String>) {
    runApplication<FprApplication>(*args)
}
