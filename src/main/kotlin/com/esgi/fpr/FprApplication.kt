package com.esgi.fpr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JprApplication

fun main(args: Array<String>) {
    runApplication<JprApplication>(*args)
}
