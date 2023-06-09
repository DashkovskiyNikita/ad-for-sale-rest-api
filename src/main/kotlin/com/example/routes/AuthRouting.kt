package com.example.routes

import com.example.repositories.AuthRepository
import com.example.utils.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.*
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
class User {

    @Resource("info")
    class Info(val user: User = User())

    @Resource("login")
    class Login(val user: User = User())

    @Resource("register")
    class Register(val user: User = User())

    @Resource("update")
    class Update(val user: User = User())

    @Resource("delete")
    class Delete(val user: User = User())

    @Resource("refresh")
    class Refresh(val user: User = User())

}

fun Route.authRouting() {

    val accessConfig: JwtConfig by inject(qualifier = named("access"))
    val refreshConfig: JwtConfig by inject(qualifier = named("refresh"))
    val authRepository: AuthRepository by inject()

    post<User.Login> {

        val request = call.receive<LoginRequest>()

        val user = authRepository.getUserByLogin(login = request.login)
            ?: throw Exception("Wrong login or password")

        val decryptedPwd = PwdUtils.decrypt(pwd = user.password)

        if (decryptedPwd == request.password) {
            val access = JwtUtils.generate(config = accessConfig, "id" to user.id.value)
            val refresh = JwtUtils.generate(config = refreshConfig, "id" to user.id.value)
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

            val encryptedPwd = PwdUtils.encrypt(pwd = request.password)

            val userWithHashedPwd = request.copy(password = encryptedPwd)
            val newUserId = authRepository.insertUser(userWithHashedPwd)

            val access = JwtUtils.generate(config = accessConfig, "id" to newUserId)
            val refresh = JwtUtils.generate(config = refreshConfig,"id" to newUserId)

            val response = TokenResponse(access = access, refresh = refresh)
            call.respond(HttpStatusCode.OK, response)

        } else {
            call.respond(HttpStatusCode.BadRequest, "User with specified phone or email already exists")
        }
    }
    authenticate {
        post<User.Refresh> {
            val principal = call.getPrincipalOrThrow()
            val access = JwtUtils.generate(
                config = accessConfig,
                "id" to principal.payload.getClaim("id").asInt()
            )
            val refresh = JwtUtils.generate(
                config = refreshConfig,
                "id" to principal.payload.getClaim("id").asInt()
            )
            val response = TokenResponse(access = access, refresh = refresh)
            call.respond(HttpStatusCode.OK, response)
        }
        put<User.Update> {
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<RegisterRequest>()
            authRepository.updateUser(
                id = principal.payload.getClaim("id").asInt(),
                request = request
            )
            call.respond(HttpStatusCode.OK)
        }
        get<User.Info>{
            val principal = call.getPrincipalOrThrow()
            val user = authRepository.getUserById(id = principal.payload.getClaim("id").asInt())
            call.respond(HttpStatusCode.OK,user)
        }
        delete<User.Delete> {
            val principal = call.getPrincipalOrThrow()
            authRepository.deleteAccount(id = principal.payload.getClaim("id").asInt())
            call.respond(HttpStatusCode.OK)
        }
    }

}

fun ApplicationCall.getPrincipalOrThrow(): JWTPrincipal {
    return principal() ?: throw Exception("Invalid principal")
}