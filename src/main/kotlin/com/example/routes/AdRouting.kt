package com.example.routes

import com.example.repositories.AdRepository
import com.example.repositories.ImageRepository
import com.example.tables.AdEntity
import com.example.tables.mapToResponse
import com.example.utils.Constants
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
import org.koin.ktor.ext.inject
import java.io.File

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
    val createdAt: String,
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

    @Resource("/all")
    class All(val page: Int? = null)

    @Resource("/search/{query}")
    class Search(val query: String)
}

fun Route.adRouting() {

    val adRepository: AdRepository by inject()
    val imageRepository: ImageRepository by inject()

    authenticate {
        get<UserAd.All> {
            val principal = call.getPrincipalOrThrow()
            val userId = principal.payload.getClaim("id").asInt()
            val userAds = adRepository.getAllAdsByUserId(userId = userId)
            val response = userAds.map(AdEntity::mapToResponse)
            call.respond(HttpStatusCode.OK, response)
        }
        post<UserAd> {
            val principal = call.getPrincipalOrThrow()
            val request = call.receive<AdRequest>()
            val userId = principal.payload.getClaim("id").asInt()
            adRepository.insertAd(userId = userId, adRequest = request)
            call.respond(HttpStatusCode.OK)
        }
        put<UserAd.Id> { param ->
            val request = call.receive<AdRequest>()
            adRepository.updateAd(id = param.id, adRequest = request)
            call.respond(HttpStatusCode.OK)
        }
        delete<UserAd.Id> { param ->

            val adImages = imageRepository.getAllImagesByAd(adId = param.id)

            adImages.forEach {
                File("${Constants.IMAGE_FOLDER}/${it.fileName}").delete()
            }

            adImages
                .map { it.id.value }
                .forEach { imageRepository.deleteImageById(id = it) }

            adRepository.deleteAdById(id = param.id)
            call.respond(HttpStatusCode.OK)
        }
    }
    get<Ad.All> { param ->
        val response = adRepository.getAds(page = param.page)
        call.respond(HttpStatusCode.OK, response)
    }
    get<Ad.Search> { param ->
        val queryResult = adRepository.searchAd(pattern = param.query)
        val response = queryResult.map(AdEntity::mapToResponse)
        call.respond(HttpStatusCode.OK, response)
    }
}

