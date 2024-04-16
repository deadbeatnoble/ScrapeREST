package com.example.route

import com.example.core.srpWholePage
import com.example.data.AuthorModel
import com.example.data.MangaModel
import com.example.data.FeedResponseModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

fun Route.feedRoute() {
    route("/feed") {
        get("?type={type?}&page={page?}") {
            //latest - for latest manga list - http://127.0.0.1:8080/feed?type=latest&page=1
            //newest - for new manga list - http://127.0.0.1:8080/feed?type=newest&page=1
            //topview - for popular manga list - http://127.0.0.1:8080/feed?type=topview&page=1

            val type = call.parameters["type"] ?: return@get call.respondText(
                "Missing type parameter",
                status = HttpStatusCode.BadRequest
            )
            val page = call.parameters["page"] ?: return@get call.respondText(
                "Missing page parameter",
                status = HttpStatusCode.BadRequest
            )

            val response = srpFeed(
                html = srpWholePage(baseUrl = "https://manganato.com/genre-all/$page?type=$type")
            )

            call.respond(response)
        }
    }
}

fun srpFeed(
    html: String
): FeedResponseModel {

    //val html = srpPopularDoc(baseUrl = "https://manganato.com/genre-all?type=topview")
    //val html = Document.popularDoc

    val list = mutableListOf<MangaModel>()

    val document = Jsoup.parse(html)
    val genre_items = document.getElementsByClass("panel-content-genres").select("div.content-genres-item")
    val total = document.getElementsByClass("panel-page-number").select("div.group-qty a").text().split(":")[1].replace(",", "")
    val page = document.getElementsByClass("panel-page-number").select("div.group-page a.page-select").text()

    val input = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-blue.page-last").text()
    val regex = Regex("\\((\\d+)\\)$")
    val matchResult = regex.find(input)
    val number = matchResult?.groups?.get(1)?.value
    val hasNextPage = page.toInt() != number?.toInt()

    //val first_genre_item = genre_items.select("div.content-genres-item").first()

    for (genre_item in genre_items) {
        genre_item?.let {
            val author = mutableListOf<AuthorModel>()
            val author_names = it.select("span.genres-item-author").text().split(",").forEach {it
                author.add(
                    AuthorModel(
                        name = it,
                        url = null
                    )
                )
            }
            list.add(
                MangaModel(
                    title = it.select("h3 a").text() ?: null,
                    thumbnail = it.select("a img").attr("src") ?: null,
                    authors = author,
                    genres = emptyList(),
                    status = null,
                    updatedAt = it.select("span.genres-item-time").text() ?: null,
                    description = it.select("div.genres-item-description").text() ?: null,
                    view = it.select("span.genres-item-view").text() ?: null,
                    rating = it.select("a").first()?.select("em.genres-item-rate")?.text() ?: null,
                    mangaUrl = it.select("a").first()?.attr("href") ?: null,
                    chapterList = emptyList()
                )
            )
        } ?: list.add(
            MangaModel(
                title = null,
                thumbnail = null,
                authors = emptyList(),
                genres = emptyList(),
                status = null,
                updatedAt = null,
                description = null,
                view = null,
                rating = null,
                mangaUrl = null,
                chapterList = emptyList()
            )
        )
    }

    return FeedResponseModel(
        response = "ok",
        data = list,
        limit = list.size,
        total = total.trim().toInt(),
        page = page.trim().toInt(),
        hasNextPage = hasNextPage
    )
}





