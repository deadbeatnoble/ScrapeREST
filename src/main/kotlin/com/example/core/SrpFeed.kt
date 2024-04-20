package com.example.core

import com.example.data.AuthorModel
import com.example.data.FeedResponseModel
import com.example.data.MangaModel
import org.jsoup.Jsoup

fun srpFeed(
    html: String,
    feedResponseListener: FeedResponseListener
) {
    val mangaList = mutableListOf<MangaModel>()

    val document = Jsoup.parse(html)

    lateinit var total: String
    lateinit var page: String
    var hasNextPage: Boolean

    document.getElementsByClass("panel-content-genres").firstOrNull()?.let { genreItems ->
        genreItems.children().forEach { genreItem ->
            val author = mutableListOf<AuthorModel>()

            genreItem.select("span.genres-item-author").firstOrNull()?.let {
                it.text().split(",").forEach { name ->
                    author.add(
                        AuthorModel(
                            name = name,
                            url = null
                        )
                    )
                }
            }

            mangaList.add(
                MangaModel(
                    title = genreItem.select("h3 a").text() ?: null,
                    thumbnail = genreItem.select("a img").attr("src") ?: null,
                    authors = author,
                    genres = emptyList(),
                    status = null,
                    updatedAt = genreItem.select("span.genres-item-time").text() ?: null,
                    description = genreItem.select("div.genres-item-description").text() ?: null,
                    view = genreItem.select("span.genres-item-view").text() ?: null,
                    rating = genreItem.select("a").first()?.select("em.genres-item-rate")?.text(),
                    mangaUrl = genreItem.select("a").first()?.attr("href"),
                    chapterList = emptyList()
                )
            )

        }
    } ?: feedResponseListener.onFailure(
        message = "Unable to PARSE the page from"
    )

    if (document.getElementsByClass("panel-page-number").isNullOrEmpty()) {
        total = mangaList.size.toString()
        page = "1"
        hasNextPage = false
    } else {
        total = document.getElementsByClass("panel-page-number").select("div.group-qty a").text().split(":")[1].replace(",", "")
        page = document.getElementsByClass("panel-page-number").select("div.group-page a.page-select").text()

        val input = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-blue.page-last").text()
        val regex = Regex("\\((\\d+)\\)$")
        val matchResult = regex.find(input)
        val number = matchResult?.groups?.get(1)?.value
        hasNextPage = page.toInt() != number?.toInt()
    }

    feedResponseListener.onSuccess(
        data = FeedResponseModel(
            response = "ok",
            data = mangaList,
            limit = mangaList.size,
            total = total.trim().toInt(),
            page = page.trim().toInt(),
            hasNextPage = hasNextPage
        )
    )
}