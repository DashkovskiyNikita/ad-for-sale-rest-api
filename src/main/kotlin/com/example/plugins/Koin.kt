package com.example.plugins

import com.example.di.adModule
import com.example.di.authModule
import com.example.di.favoriteModule
import com.example.di.imageModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(authModule, adModule, favoriteModule, imageModule)
    }
}