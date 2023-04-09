package com.example.di

import com.example.repositories.AuthRepository
import com.example.repositories.AuthRepositoryImpl
import com.example.utils.Env
import com.example.utils.JwtConfig
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Duration

val authModule = module {

    val accessConfig = JwtConfig(
        secret = Env.JWT_SECRET,
        issuer = Env.JWT_ISSUER,
        audience = Env.JWT_AUDIENCE,
        expiresIn = Duration.ofDays(1).toMillis()
    )

    single(qualifier = named("access")) {
        accessConfig
    }

    single(qualifier = named("refresh")) {
        accessConfig.copy(expiresIn = Duration.ofDays(3).toMillis())
    }

    singleOf(::AuthRepositoryImpl) {
        bind<AuthRepository>()
    }
}