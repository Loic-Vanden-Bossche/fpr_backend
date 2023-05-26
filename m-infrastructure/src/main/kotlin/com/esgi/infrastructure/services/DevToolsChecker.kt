package com.esgi.infrastructure.services

import org.springframework.stereotype.Service

@Service
class DevToolsChecker {
    fun isDevToolsPresent(): Boolean {
        return try {
            Class.forName("org.springframework.boot.devtools.classpath.ClassPathRestartStrategy")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}