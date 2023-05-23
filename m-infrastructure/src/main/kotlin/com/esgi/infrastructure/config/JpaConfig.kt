package com.esgi.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
class JpaConfig {
    @Bean
    fun dateTimeProvider() = DateTimeProvider {
        Optional.of(ZonedDateTime.now(ZoneId.of("UTC")).toInstant())
    }
}