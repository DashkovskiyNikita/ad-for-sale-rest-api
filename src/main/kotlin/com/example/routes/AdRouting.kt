package com.example.routes

import com.example.repositories.AdRepository
import com.example.repositories.ImageRepository
import com.example.tables.AdEntity
import com.example.tables.mapToAdResponse
import com.example.tables.mapToUserAdResponse
import com.example.utils.Constants
import io.ktor.http.*
import io.ktor.http.content.*
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
import java.util.UUID

@Serializable
data class AdRequest(
    val title: String,
    val description: String,
    val price: Int,
    val currency: String
)

class AdForm(map: Map<String, Any?>) {
    val title: String by map
    val description: String by map
    val price: Int
    val currency: String by map

    init {
        price = map["price"].toString().toInt()
    }
}

@Serializable
data class UserAdPhoto(
    val id: Int,
    val imageUrl: String
)

@Serializable
data class UserAdResponse(
    val id: Int,
    val title: String,
    val description: String,
    val createdAt: String,
    val price: Int,
    val currency: String,
    val photos: List<UserAdPhoto>
)

@Serializable
data class Author(
    val id: Int,
    val name: String,
    val surname: String,
    val phone: String
)

@Serializable
data class AdResponse(
    val id: Int,
    val author: Author,
    val title: String,
    val description: String,
    val createdAt: String,
    val price: Int,
    val currency: String,
    val photos: List<UserAdPhoto>
)

@Resource("user/ad")
class UserAd {

    @Resource("/{id}")
    class Id(val userAd: UserAd = UserAd(), val id: Int)

    @Resource("/all")
    class All(val userAd: UserAd = UserAd())

}

@Resource("/ad")
class Ad {

    @Resource("/all")
    class All(val ad: Ad = Ad(), val page: Int? = null)

    @Resource("/{id}")
    class Id(val ad: Ad = Ad(), val id: Int)

    @Resource("/search/{query}")
    class Search(val ad: Ad = Ad(), val query: String)
}

fun Route.adRouting() {

    val adRepository: AdRepository by inject()
    val imageRepository: ImageRepository by inject()

    authenticate {
        get<UserAd.All> {
            val principal = call.getPrincipalOrThrow()
            val userId = principal.payload.getClaim("id").asInt()
            val userAds = adRepository.getAllAdsByUserId(userId = userId)
            val response = userAds.map(AdEntity::mapToUserAdResponse)
            call.respond(HttpStatusCode.OK, response)
        }
        post<UserAd> {
            val principal = call.getPrincipalOrThrow()
            val multiPart = call.receiveMultipart()

            val map = mutableMapOf<String, Any?>()
            val images = mutableListOf<String>()

            multiPart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        partData.name?.let { name ->
                            map.put(name, partData.value)
                        }
                    }

                    is PartData.FileItem -> {
                        val fileName = partData.saveImage()
                        images.add(fileName)
                    }

                    else -> {}
                }
                partData.dispose()
            }

            val userId = principal.payload.getClaim("id").asInt()
            val adForm = AdForm(map = map)
            val adId = adRepository.insertAd(userId = userId, adForm = adForm)
            images.forEach { fileName ->
                imageRepository.insertImage(adId = adId, fileName = fileName)
            }
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
    get<Ad.Id> { param ->
        val ad = adRepository.getAdByID(id = param.id)
        val response = ad.mapToAdResponse()
        call.respond(HttpStatusCode.OK,response)
    }
    get<Ad.All> { param ->
        val adsList = adRepository.getAds(page = param.page)
        val response = adsList.map(AdEntity::mapToAdResponse)
        call.respond(HttpStatusCode.OK, response)
    }
    get<Ad.Search> { param ->
        val queryResult = adRepository.searchAd(pattern = param.query)
        val response = queryResult.map(AdEntity::mapToAdResponse)
        call.respond(HttpStatusCode.OK, response)
    }
}

private const val IMAGE_EXT = ".jpg"
fun PartData.FileItem.saveImage(): String {
    val uuid = UUID.randomUUID().toString()
    val fileName = "$uuid$IMAGE_EXT"
    val path = "${Constants.IMAGE_FOLDER}/$fileName"
    val bytes = streamProvider().readBytes()
    File(path).writeBytes(bytes)
    return fileName
}

