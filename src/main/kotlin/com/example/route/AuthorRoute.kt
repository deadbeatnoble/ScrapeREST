package com.example.route

import com.example.core.srpWholePage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authorRoute() {
    route("/author") {
        get("?author_id={author_id?}&page={page?}") {
            val author_id = call.parameters["author_id"] ?: return@get call.respondText(
                "Missing author_id parameter",
                status = HttpStatusCode.BadRequest
            )

            val page = call.parameters["page"] ?: "1"

            val url = "https://manganato.com/author/story/${author_id}?page=${page}"

            call.respond(srpSearch(html = srpWholePage(baseUrl = url)))
        }
    }
}