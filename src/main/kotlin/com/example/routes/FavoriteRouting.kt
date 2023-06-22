package com.example.routes

import com.example.repositories.FavoriteRepository
import com.example.tables.FavoriteEntity
import com.example.tables.mapToUserAdResponse
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

@Resource("user/ad/{id}/favorite")
class DeleteFavoriteByAdId(val id : Int)

@Resource("user/favorites")
object GetFavorites

@Serializable
data class Favorite(
    val id: Int,
    val ad: UserAdResponse
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
            val response = favorites.map(FavoriteEntity::mapToUserAdResponse)
            call.respond(HttpStatusCode.OK, response)
        }
        delete<DeleteFavorite> { param ->
            favoriteRepository.deleteFavorite(id = param.id)
            call.respond(HttpStatusCode.OK)
        }
        delete<DeleteFavoriteByAdId>{ param ->
            favoriteRepository.deleteFavoriteByAdId(adId = param.id)
            call.respond(HttpStatusCode.OK)
        }
    }
}