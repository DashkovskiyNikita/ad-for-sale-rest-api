package com.example

import com.example.di.adModule
import com.example.di.authModule
import com.example.plugins.configureDatabase
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.routes.LoginRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.lang.Exception
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Before
    fun setup() {
        startKoin {
            modules(authModule, adModule)
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun loginTest() = testApplication {
        application {
            install(Resources)
            configureSerialization()
            configureSecurity()
            configureRouting()
            configureDatabase()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/user/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    login = "88005553535",
                    password = "password"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun loginTestWithNotExistedUser(){
        testApplication {
            application {
                install(Resources)
                configureSerialization()
                configureSecurity()
                configureRouting()
                configureDatabase()
            }
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }
            Assert.assertThrows(Exception::class.java){
                runBlocking {
                    client.post("/user/login") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            LoginRequest(
                                login = "89644013345",
                                password = "password"
                            )
                        )
                    }
                }
            }
        }
    }

    @Test
    fun getAllAdsEndpointTest() = testApplication {
        application {
            install(Resources)
            configureSerialization()
            configureSecurity()
            configureRouting()
            configureDatabase()
        }
        val response = client.get("/ad/all")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
