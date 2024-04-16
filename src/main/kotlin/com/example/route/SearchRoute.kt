package com.example.route

import com.example.core.srpWholePage
import com.example.data.AuthorModel
import com.example.data.FeedResponseModel
import com.example.data.MangaModel
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

fun Route.searchRoute() {
    route("/search") {
        get("?title={title?}&page={page?}") {
            val title = call.parameters["title"] ?: return@get call.respondText(
                "Missing title parameter",
                status = HttpStatusCode.BadRequest
            )
            val page = call.parameters["page"] ?: "1"
            val list = srpSearch(html = srpWholePage(baseUrl = "https://manganato.com/search/story/$title?page=${page}"))

            call.respond(list)
        }
    }
}

fun srpSearch(
    html: String
): FeedResponseModel {
    //val html = Document.searchDoc
    val document = Jsoup.parse(html)
    val list = mutableListOf<MangaModel>()

    val story_items = document.getElementsByClass("panel-search-story").select("div.search-story-item")

    for (story_item in story_items) {
        story_item?.let {
            val author_names = it.getElementsByClass("item-right").select("span.item-author").text().split(",")
            val author = mutableListOf<AuthorModel>()
            for (author_name in author_names) {
                author_name?.let {
                    author.add(
                        AuthorModel(
                            name = it,
                            url = null
                        )
                    )
                }
            }
            list.add(
                MangaModel(
                    title = it.getElementsByClass("item-right").select("h3 a").text() ?: null,
                    thumbnail = it.select("a img").attr("src") ?: null,
                    authors = author,
                    genres = emptyList(),
                    status = null,
                    updatedAt = it.getElementsByClass("item-right").select("span.item-time").first()?.text()?.split(":", limit = 2)?.get(1)?.trim() ?: null,
                    description = null,
                    view = it.getElementsByClass("item-right").select("span.item-time")[1].text().split(":")[1].trim(),
                    rating = it.select("a").select("em.item-rate").text() ?: null,
                    mangaUrl = it.select("a").attr("href") ?: null,
                    chapterList = emptyList()
                )
            )
        }
    }

    var total = ""
    var page = ""
    var hasNextPage = false

    if (document.getElementsByClass("panel-page-number").isNullOrEmpty()) {
        total = list.size.toString()
        page = "1"
        hasNextPage = false
    } else {
        total = document.getElementsByClass("panel-page-number").select("div.group-qty").select("a").text().split(":")[1].trim().replace(",", "")
        page = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-blue")[1].text()

        val input = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-last").text()
        val regex = Regex("\\((\\d+)\\)$")
        val matchResult = regex.find(input)
        val number = matchResult?.groups?.get(1)?.value
        hasNextPage = page.toInt() != number?.toInt()
    }

    return FeedResponseModel(
        response = "ok",
        data = list,
        limit = list.size,
        total = total.toInt(),
        page = page.toInt(),
        hasNextPage = hasNextPage
    )
}