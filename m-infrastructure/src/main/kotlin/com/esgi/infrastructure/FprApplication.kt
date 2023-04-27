package com.esgi.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FprApplication

fun main(args: Array<String>) {
    runApplication<FprApplication>(*args)
}
