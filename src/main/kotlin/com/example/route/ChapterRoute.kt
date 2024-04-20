package com.example.route

import com.example.core.ChapterResponseListener
import com.example.core.ResponseListener
import com.example.core.srpChapter
import com.example.core.srpWholePage
import com.example.data.ChapterResponseModel
import com.example.data.ErrorResponseModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.chapterRoute() {
    route("/chapter") {
        get("?manga_id={manga_id?}&chapter_id={chapter_id?}") {
            //chapter - https://chapmanganato.to/manga-aa951409/chapter-1111
            val manga_id = call.parameters["manga_id"] ?: return@get call.respondText(
                "Missing parameter manga_id",
                status = HttpStatusCode.BadRequest
            )

            val chapter_id = call.parameters["chapter_id"] ?: return@get call.respondText(
                "Missing parameter chapter_id",
                status = HttpStatusCode.BadRequest
            )

            val url = "https://chapmanganato.to/${manga_id}/${chapter_id}"

            var successResponse = ChapterResponseModel(
                response = "",
                data = emptyList(),
                referrer = ""
            )
            var errorResponse = ErrorResponseModel(
                response = "",
                error = ""
            )

            srpWholePage(
                baseUrl = url,
                responseListener = object: ResponseListener {
                    override fun onSuccess(data: String) {
                        srpChapter(
                            html = data,
                            referrer = url,
                            chapterResponseListener = object: ChapterResponseListener {
                                override fun onSuccess(data: ChapterResponseModel) {
                                    successResponse = data
                                }

                                override fun onFailure(message: String) {
                                    errorResponse = ErrorResponseModel(
                                        response = "ok",
                                        error = "$message $url"
                                    )
                                }

                            }
                        )
                    }

                    override fun onFailure(message: String) {
                        errorResponse = ErrorResponseModel(
                            response = "ok",
                            error = message
                        )
                    }

                }
            )

            val response = if (successResponse.data.isEmpty()) errorResponse else successResponse

            call.respond(response)
        }
    }
}