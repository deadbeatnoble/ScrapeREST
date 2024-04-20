package com.example.route

import com.example.core.FeedResponseListener
import com.example.core.ResponseListener
import com.example.core.srpAdvancedSearch
import com.example.core.srpWholePage
import com.example.data.ErrorResponseModel
import com.example.data.FeedResponseModel
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.advancedSearchRoute() {
    route("/advanced_search") {
        get("?type={type?}&title={title?}&s={s?}&g_i={g_i?}&g_e={g_e?}&stat={stat?}&orby={orby?}&page={page?}") {
            //type -> - (everything),title, alternative, author
            //title -> word
            //stat -> ongoing, completed, - (ongoing and completed)
            //orby -> - (latest), topview, newest, az

            val type = call.parameters["type"]
            val title = call.parameters["title"]
            val s = "all"
            val g_i = call.parameters["g_i"]?.split("_")
            val g_e = call.parameters["g_e"]?.split("_")
            val stat = call.parameters["stat"]
            val orby = call.parameters["orby"]
            val page = call.parameters["page"]?.toInt() ?: 1

            val url = buildString {
                append("https://manganato.com/advanced_search?s=${s}")
                g_i?.let {append("&g_i=_${g_i.joinToString("_")}_")}
                g_e?.let {append("&g_e=_${g_e.joinToString("_")}_")}
                stat?.let { append("&sts=${stat}") }
                orby?.let { append("&orby=${orby}") }
                append("&page=${page}")
                type?.let { append("&keyt=${type}") }
                title?.let { append("&keyw=${title}") }
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
                        srpAdvancedSearch(
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