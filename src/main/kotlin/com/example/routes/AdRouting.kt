package com.example.routes

import com.example.repositories.AdRepository
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AdRequest(
    val title: String,
    val description: String,
    val price: Int,
    val currency: String
)

@Serializable
data class AdResponse(
    val id: Int,
    val title: String,
    val description: String,
    val cratedAt: String,
    val price: Int,
    val currency: String
)

@Resource("user/ad")
object UserAd {

    @Resource("/{id}")
    class Id(val id: Int)

    @Resource("/all")
    object All

}

@Resource("/ad")
object Ad {
    @Resource("/{id}")
    class Id(val id: Int)

    @Resource("/all")
    class All(val page: Int? = null)

    @Resource("/search/{query}")
    class Search(val query: String)
}

fun Route.adRouting(adRepository: AdRepository) {
    authenticate {
        get<UserAd.All> {
            val principal = call.getPrincipalOrThrow()
            val userId = principal.payload.getClaim("id").asInt()
            val userAds = adRepository.getAllAdsByUserId(userId = userId)
            //todo return user's ads
        }
        post<UserAd> {
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<AdRequest>()
            val userId = principal.payload.getClaim("id").asInt()
            adRepository.insertAd(userId = userId, adRequest = request)
            call.respond(HttpStatusCode.BadRequest)
        }
        put<UserAd.Id> { param ->
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<AdRequest>()
            val userId = principal.payload.getClaim("id").asInt()
            //todo:
            // 1.check if user owns this ad
            // 2.update ad
        }
        delete<UserAd.Id> { param ->
            val principal = call.getPrincipalOrThrow()
            //todo:
            // 1. get ad by id
            // 2. check if ad author id equals user id
            // 3. delete ad and images referenced on ad id or throw exception
        }
    }
    get<Ad.Id> { param ->
        //todo get ad by id
    }
    get<Ad.All> { param ->
        //todo get all ads or ads specified page
    }
    get<Ad.Search> { param ->
        val queryResult = adRepository.searchAd(pattern = param.query)
    }
}

