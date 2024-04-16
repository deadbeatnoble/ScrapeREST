package com.example.route

import com.example.core.srpWholePage
import com.example.data.ChapterResponseModel
import com.example.data.PageModel
import com.example.util.Document
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

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

            call.respond(srpChapter(html = srpWholePage(baseUrl = url), referrer = url))
        }
    }
}

fun srpChapter(
    html: String,
    referrer: String
): ChapterResponseModel {

    val document = Jsoup.parse(html)
    val pageList = mutableListOf<PageModel>()

    val chapter_reader_list = document.getElementsByClass("container-chapter-reader").select("img")

    for (chapter_reader in  chapter_reader_list) {
        chapter_reader?.let {
            pageList.add(
                PageModel(
                    title = it.attr("title"),
                    pageImageUrl = it.attr("src")
                )
            )
        } ?: pageList.add(
            PageModel(
                title = null,
                pageImageUrl = null
            )
        )
    }


    return ChapterResponseModel(
        response = "ok",
        data = pageList,
        referrer = referrer
    )
}