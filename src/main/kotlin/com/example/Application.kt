package com.example

import com.example.plugins.*
import com.example.utils.Env
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = Env.PORT,
        host = Env.HOST,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureCORS()
    install(Resources)
    configureKoin()
    configureSerialization()
    configureDatabase()
    configureSecurity()
    configureRouting()
    configureFolders()
    install(StatusPages)
}
