package com.example.routes

import com.example.repositories.FavoriteRepository
import com.example.tables.FavoriteEntity
import com.example.tables.mapToResponse
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Resource("user/favorite/{id}")
class NewFavorite(val id: Int)

@Resource("user/favorite/{id}")
class DeleteFavorite(val id: Int)

@Resource("favorite/all")
object GetFavorites

@Serializable
data class Favorite(
    val id: Int,
    val ad: AdResponse
)

fun Route.favoriteRouting() {

    val favoriteRepository: FavoriteRepository by inject()

    authenticate {
        post<NewFavorite> { param ->
            val principal = call.getPrincipalOrThrow()
            val userId = principal.payload.getClaim("id").asInt()
            favoriteRepository.insertFavorite(userId = userId, adId = param.id)
            call.respond(HttpStatusCode.OK)
        }
        get<GetFavorites> {
            val principal = call.getPrincipalOrThrow()
            val userId = principal.payload.getClaim("id").asInt()
            val favorites = favoriteRepository.getAllFavoritesByUser(userId = userId)
            val response = favorites.map(FavoriteEntity::mapToResponse)
            call.respond(HttpStatusCode.OK, response)
        }
        delete<DeleteFavorite> { param ->
            favoriteRepository.deleteFavorite(adId = param.id)
            call.respond(HttpStatusCode.OK)
        }
    }
}