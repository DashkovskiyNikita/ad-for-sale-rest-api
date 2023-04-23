package com.example.routes

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
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

fun Route.adRouting() {
    authenticate {
        get<UserAd.All> {
            val principal = call.getPrincipalOrThrow()
            //todo get ads where author id equals user id
        }
        post<UserAd> {
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<AdRequest>()
            //todo add new ad to repository
        }
        put<UserAd.Id> { param ->
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<AdRequest>()
            //todo:
            // 1.get ad by id
            // 2.check if ad author id equals user id
            // 3.update ad or throw exception
        }
        delete<UserAd.Id> { param ->
            val principal = call.getPrincipalOrThrow()
            //todo:
            // 1. get ad by id
            // 2. check if ad author id equals user id
            // 3. delete ad and images referenced on ad id or throw exception
        }
    }
    get<Ad.Id> {
        //todo get ad by id
    }
    get<Ad.All> {
        //todo get all ads or ads specified page
    }
    get<Ad.Search>{
        //todo search ads by query and return it
    }
}

