package com.example.route

import com.example.core.srpWholePage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.genreRoute() {
    route("/genre") {
        get("?genre_id={genre_id?}&page={page?}") {
            val genre_id = call.parameters["genre_id"] ?: return@get call.respondText(
                "Missing genre_id parameter",
                status = HttpStatusCode.BadRequest
            )

            val page = call.parameters["page"] ?: "1"

            val url = "https://manganato.com/genre-${genre_id}/${page}"

            call.respond(srpFeed(html = srpWholePage(baseUrl = url)))
        }
    }
}