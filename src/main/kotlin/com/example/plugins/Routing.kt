package com.example.plugins

import com.example.routes.adRouting
import com.example.routes.authRouting
import com.example.routes.favoriteRouting
import com.example.routes.imageRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        adRouting()
        imageRouting()
        favoriteRouting()
    }
}
