package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.utils.JwtConfig
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {

    val jwtConfig: JwtConfig by inject(qualifier = named("access"))

    authentication {
        jwt {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience))
                    JWTPrincipal(credential.payload)
                else
                    null
            }
        }
    }
}
