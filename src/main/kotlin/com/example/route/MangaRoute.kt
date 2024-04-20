package com.example.route

import com.example.core.MangaResponseListener
import com.example.core.ResponseListener
import com.example.core.srpMangaDetail
import com.example.core.srpWholePage
import com.example.data.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

fun Route.mangaRoute() {
    route("/manga") {
        get("?manga_id={manga_id?}") {
            //manga - http://127.0.0.1:8080/manga/manga-yu1001655

            val manga_id = call.parameters["manga_id"] ?: return@get call.respondText(
                "Missing Parameter id",
                status = HttpStatusCode.BadRequest
            )
            val url = "https://chapmanganato.to/$manga_id"

            var successResponse = MangaModel(
                title = "",
                thumbnail = "",
                authors = emptyList(),
                genres = emptyList(),
                status = "",
                updatedAt = "",
                description = "",
                view = "",
                rating = "",
                mangaUrl = "",
                chapterList = emptyList()
            )
            var errorResponse = ErrorResponseModel(
                response = "",
                error = ""
            )

            srpWholePage(
                baseUrl = url,
                responseListener = object: ResponseListener {
                    override fun onSuccess(data: String) {
                        srpMangaDetail(
                            html = data,
                            mangaUrl = url,
                            mangaResponseListener = object: MangaResponseListener {
                                override fun onSuccess(data: MangaModel) {
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

            val response = if (successResponse.mangaUrl.isNullOrEmpty()) errorResponse else successResponse

            call.respond(response)
        }
    }
}