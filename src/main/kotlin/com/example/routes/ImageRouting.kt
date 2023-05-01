package com.example.routes

import com.example.repositories.ImageRepository
import com.example.utils.Constants
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.UUID

@Resource("user/ad/{id}/image")
class PostImage(val adId: Int)

@Resource("/image/{fileName}")
class GetImage(val fileName: String)

@Resource("/image/{id}")
class DeleteImage(val id: Int)

fun Route.imageRouting() {

    val imageRepository : ImageRepository by inject()

    authenticate {
        post<PostImage> { param ->
            //todo check if user owns ad's id
            val imageDirectory = File(Constants.IMAGE_FOLDER)
            if (!imageDirectory.exists()) imageDirectory.mkdir()
            val fileName = UUID.randomUUID().toString()
            val file = File("${Constants.IMAGE_FOLDER}/$fileName.jpg")
            call.receiveChannel().copyAndClose(file.writeChannel())
            imageRepository.insertImage(adId = param.adId, fileName = fileName)
            call.respond(HttpStatusCode.OK)
        }
        delete<DeleteImage> { param ->
            val image = imageRepository.getImageById(id = param.id)
            File("${Constants.IMAGE_FOLDER}/${image.fileName}").delete()
            imageRepository.deleteImageById(id = param.id)

        }
    }
    get<GetImage> { param ->
        val file = File("${Constants.IMAGE_FOLDER}/${param.fileName}")
        if (file.exists())
            call.respondFile(file)
        else
            call.respond(HttpStatusCode.BadRequest,"File not exists")
    }
}