package com.example.routes

import com.example.repositories.AuthRepository
import com.example.utils.CryptUtils
import com.example.utils.Env
import com.example.utils.JwtConfig
import com.example.utils.JwtUtils
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val surname: String,
    val password: String,
    val phone: String,
    val email: String
)

@Serializable
data class UserInfoResponse(
    val name: String,
    val surname: String,
    val phone: String,
    val email: String?
)

@Serializable
data class TokenResponse(
    val access: String,
    val refresh: String
)

@Resource("/user")
object User {

    @Resource("/login")
    object Login

    @Resource("/register")
    object Register

    @Resource("/update")
    object Update

    @Resource("/delete")
    object Delete

    @Resource("/refresh")
    object Refresh

}

fun Route.authRouting() {

    val accessConfig by inject<JwtConfig>(qualifier = named("access"))
    val refreshConfig by inject<JwtConfig>(qualifier = named("refresh"))
    val authRepository by inject<AuthRepository>()

    post<User.Login> {

        val request = call.receive<LoginRequest>()

        val user = authRepository.getUserByLogin(login = request.login) ?: throw Exception()

        val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = Env.PWD_SALT)
        val decryptedPwd = CryptUtils.decrypt(data = user.password, key = key)

        if (decryptedPwd == request.password) {
            val access = JwtUtils.generate(config = accessConfig, "id" to user.id.value)
            val refresh = JwtUtils.generate(config = refreshConfig)
            val response = TokenResponse(access = access, refresh = refresh)
            call.respond(HttpStatusCode.OK, response)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Wrong login or password")
        }
    }
    post<User.Register> {
        val request = call.receive<RegisterRequest>()
        val user = authRepository.getUserByPhoneOrEmail(phone = request.phone, email = request.email)
        if (user == null) {

            val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = Env.PWD_SALT)
            val encryptedPwd = CryptUtils.encrypt(data = request.password,key = key)

            val userWithHashedPwd = request.copy(password = encryptedPwd)
            val newUserId = authRepository.insertUser(userWithHashedPwd)

            val access = JwtUtils.generate(config = accessConfig, "id" to newUserId)
            val refresh = JwtUtils.generate(config = refreshConfig)

            val response = TokenResponse(access = access, refresh = refresh)
            call.respond(HttpStatusCode.OK, response)

        } else {
            call.respond(HttpStatusCode.BadRequest, "User with specified phone or email already exists")
        }
    }
    authenticate {
        post<User.Refresh> {
            val principal = call.principal<JWTPrincipal>() ?: return@post
            val access = JwtUtils.generate(
                config = accessConfig,
                "id" to principal.payload.getClaim("id").asInt()
            )
            val refresh = JwtUtils.generate(config = refreshConfig)
            val response = TokenResponse(access = access, refresh = refresh)
            call.respond(HttpStatusCode.OK, response)
        }
        put<User.Update> {
            val principal = call.principal<JWTPrincipal>() ?: return@put
            val request = call.receive<RegisterRequest>()
            authRepository.updateUser(
                id = principal.payload.getClaim("id").asInt(),
                request = request
            )
            call.respond(HttpStatusCode.OK)
        }
        delete<User.Delete> {
            val principal = call.principal<JWTPrincipal>() ?: return@delete
            authRepository.deleteAccount(id = principal.payload.getClaim("id").asInt())
            call.respond(HttpStatusCode.OK)
        }
    }

}