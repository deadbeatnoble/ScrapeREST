package com.example.route

import com.example.core.srpWholePage
import com.example.data.AuthorModel
import com.example.data.ChapterModel
import com.example.data.GenreModel
import com.example.data.MangaModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

fun Route.mangaRoute() {
    route("/manga") {
        get("?id={id?}") {
            //manga - http://127.0.0.1:8080/manga/manga-yu1001655

            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing Parameter id",
                status = HttpStatusCode.BadRequest
            )
            val html = srpWholePage(baseUrl = "https://chapmanganato.to/$id")
            val response = srpMangaDetail(
                html = html,
                mangaUrl = "https://chapmanganato.to/$id"
            )

            call.respond(response)
        }
    }
}

fun srpMangaDetail(
    html: String,
    mangaUrl: String
): MangaModel {
    //val html = Document.mangaDoc
    val document = Jsoup.parse(html)

    val tableRows = document.getElementsByClass("variations-tableInfo").select("tr")

    val authors = mutableListOf<AuthorModel>()
    var status: String? = null
    val genres = mutableListOf<GenreModel>()

    for (tableRow in tableRows) {
        when(tableRow.getElementsByClass("table-label").text().replace("\"", "").split(" ")[0]) {
            "Author(s)" -> tableRow.getElementsByClass("table-value").select("a").forEach { author ->
                authors.add(
                    AuthorModel(
                        name = author.text(),
                        url = author.attr("href") ?: null
                    )
                )
            }
            "Status" -> status = tableRow.getElementsByClass("table-value").text() ?: null
            "Genres" -> tableRow.getElementsByClass("table-value").select("a").forEach { genre ->
                genres.add(
                    GenreModel(
                        name = genre.text(),
                        url = genre.attr("href") ?: null
                    )
                )
            }
        }
    }

    val pList = document.getElementsByClass("story-info-right-extent").select("p")

    var updatedAt: String? = null
    var view: String? = null

    for (p in pList) {
        when(p.getElementsByClass("stre-label").text().replace("\"", "").split(" ")[0]) {
            "Updated" -> updatedAt = p.getElementsByClass("stre-value").text() ?: null
            "View" -> view = p.getElementsByClass("stre-value").text() ?: null
        }
    }

    val chapterList = mutableListOf<ChapterModel>()

    val story_chapter_list = document.getElementsByClass("panel-story-chapter-list").select("ul.row-content-chapter").select("li.a-h")

    for (chapter in  story_chapter_list) {
        chapter?.let {
            chapterList.add(
                ChapterModel(
                    title = it.select("a").text() ?: null,
                    view = it.select("span.chapter-view").text() ?: null,
                    uploadedAt = it.select("span.chapter-time").attr("title") ?: null,
                    chapterUrl = it.select("a").attr("href") ?: null
                )
            )
        } ?: chapterList.add(
            ChapterModel(
                title = null,
                view = null,
                uploadedAt = null,
                chapterUrl = null
            )
        )
    }



    return MangaModel(
        title = document.getElementsByClass("story-info-right").select("h1").text() ?: null,
        thumbnail = document.getElementsByClass("story-info-left").select("span.info-image").select("img").attr("src") ?: null,
        authors = authors,
        genres = genres,
        status = status,
        updatedAt = updatedAt,
        description = document.getElementsByClass("panel-story-info").select("div.panel-story-info-description").text().split(":", limit = 2)[1].trim() ?: null,
        view = view,
        rating = document.getElementById("rate_row_cmd")?.text()?.split(":")?.get(1)?.trim()?.split("/")?.get(0)?.trim() ?: null,
        mangaUrl = mangaUrl,
        chapterList = chapterList
    )
}