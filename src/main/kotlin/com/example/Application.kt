package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.utils.Env

fun main() {
    embeddedServer(
        factory = Netty,
        port = Env.PORT,
        host = Env.HOST,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabase()
    configureSecurity()
    configureRouting()
    configureKoin()
}
