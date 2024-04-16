package com.example.route

import com.example.core.srpWholePage
import com.example.data.AuthorModel
import com.example.data.FeedResponseModel
import com.example.data.MangaModel
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jsoup.Jsoup

fun Route.advancedSearchRoute() {
    route("/advanced_search") {
        get("?type={type?}&title={title?}&s={s?}&g_i={g_i?}&g_e={g_e?}&stat={stat?}&orby={orby?}&page={page?}") {
            //correct - https://manganato.com/advanced_search?s=all&g_i=_2_&g_e=_7_&page=1&keyw=naruto
            //one piece --> one_piece

            val type = call.parameters["type"]
            val title = call.parameters["title"]
            val s = "all"
            val g_i = call.parameters["g_i"]?.split("_")
            val g_e = call.parameters["g_e"]?.split("_")
            val stat = call.parameters["stat"]
            val orby = call.parameters["orby"] //newest,topview,latest="",az
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

            call.respond(srpAdvancedSearch(html = srpWholePage(baseUrl = url)))
        }
    }
}

fun srpAdvancedSearch(
    html: String
): FeedResponseModel {

    val list = mutableListOf<MangaModel>()
    val document = Jsoup.parse(html)

    val genres_items = document.getElementsByClass("panel-content-genres").select("div.content-genres-item")

    for (genres_item in genres_items) {
        genres_item?.let {
            val authors = mutableListOf<AuthorModel>()
            it.getElementsByClass("genres-item-info").select("p.genres-item-view-time").select("span.genres-item-author").text().split(",").forEach {it
                authors.add(
                    AuthorModel(
                        name = it,
                        url = null
                    )
                )
            }

            list.add(
                MangaModel(
                    title = it.getElementsByClass("genres-item-info").select("h3 a").text() ?: null,
                    thumbnail = it.select("a img").attr("src") ?: null,
                    authors = authors,
                    genres = emptyList(),
                    status = null,
                    updatedAt = it.getElementsByClass("genres-item-info").select("p.genres-item-view-time").select("span.genres-item-time").text() ?: null,
                    description = it.getElementsByClass("genres-item-info").select("div.genres-item-description").text() ?: null,
                    view = it.getElementsByClass("genres-item-info").select("p.genres-item-view-time").select("span.genres-item-view").text() ?: null,
                    rating = it.select("a").select("em.genres-item-rate").text() ?: null,
                    mangaUrl = it.select("a").attr("href"),
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
        total = document.getElementsByClass("group-qty").select("a.page-blue").text().split(":")[1].trim().replace(",", "")
        page = document.getElementsByClass("group-page").select("a.page-select").text()

        val input = document.getElementsByClass("group-page").select("a.page-last").text()
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
