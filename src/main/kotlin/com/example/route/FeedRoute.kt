package com.example.route

import com.example.core.FeedResponseListener
import com.example.core.ResponseListener
import com.example.core.srpFeed
import com.example.core.srpWholePage
import com.example.data.ErrorResponseModel
import com.example.data.FeedResponseModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.feedRoute() {
    route("/feed") {
        get("?type={type?}&page={page?}") {
            val type = call.parameters["type"] ?: return@get call.respondText(
                "Missing type parameter",
                status = HttpStatusCode.BadRequest
            )
            val page = call.parameters["page"]

            val url = buildString {
                append("https://manganato.com/genre-all")
                page?.let { append("/$page") }
                append("?type=$type")
            }

            var successResponse = FeedResponseModel(
                response = "",
                data = emptyList(),
                limit = 0,
                total = 0,
                page = 0,
                hasNextPage = false
            )
            var errorResponse = ErrorResponseModel(
                response = "",
                error = ""
            )

            srpWholePage(
                baseUrl = url,
                responseListener = object: ResponseListener {
                    override fun onSuccess(data: String) {
                        srpFeed(
                            html = data,
                            feedResponseListener = object: FeedResponseListener {
                                override fun onSuccess(data: FeedResponseModel) {
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