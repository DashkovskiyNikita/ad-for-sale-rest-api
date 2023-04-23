package com.example.routes

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post

@Resource("user/ad/{id}/image")
class PostImage(val adId: Int)

@Resource("/image/{fileName}")
class GetImage(val fileName : String)

@Resource("/image/{id}")
class DeleteImage(val id : Int)

fun Route.imageRouting() {
    authenticate {
        post<PostImage> { param ->
            val principal = call.getPrincipalOrThrow()
            //todo:
            // 1.receive image
            // 2.save image in folder
            // 3.save it to db
        }
        delete<DeleteImage>{
            val principal = call.getPrincipalOrThrow()
            //todo
            // 1.check if user owns image
            // 2.delete image by id
        }
    }
    get<GetImage> {
        //todo:
        // 1. get image from folder by fileName
        // 2. if file exists respond file otherwise throe exception
    }
}