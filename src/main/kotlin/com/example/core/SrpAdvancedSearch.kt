package com.example.core

import com.example.data.AuthorModel
import com.example.data.FeedResponseModel
import com.example.data.MangaModel
import org.jsoup.Jsoup

interface FeedResponseListener {
    fun onSuccess(data: FeedResponseModel)
    fun onFailure(message: String)
}

fun srpAdvancedSearch(
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
            val authors = mutableListOf<AuthorModel>()

            genreItem.getElementsByClass("genres-item-info").firstOrNull()?.let {
                it.select("p.genres-item-view-time").select("span.genres-item-author").text().split(",").forEach { name ->
                    authors.add(
                        AuthorModel(
                            name = name,
                            url = null
                        )
                    )
                }
            }

            mangaList.add(
                MangaModel(
                    title = genreItem.getElementsByClass("genres-item-info").select("h3 a").text() ?: null,
                    thumbnail = genreItem.select("a img").attr("src") ?: null,
                    authors = authors,
                    genres = emptyList(),
                    status = null,
                    updatedAt = genreItem.getElementsByClass("genres-item-info").select("p.genres-item-view-time").select("span.genres-item-time").text() ?: null,
                    description = genreItem.getElementsByClass("genres-item-info").select("div.genres-item-description").text() ?: null,
                    view = genreItem.getElementsByClass("genres-item-info").select("p.genres-item-view-time").select("span.genres-item-view").text() ?: null,
                    rating = genreItem.select("a").select("em.genres-item-rate").text() ?: null,
                    mangaUrl = genreItem.select("a").attr("href"),
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
        total = document.getElementsByClass("group-qty").select("a.page-blue").text().split(":")[1].trim().replace(",", "")
        page = document.getElementsByClass("group-page").select("a.page-select").text()

        val input = document.getElementsByClass("group-page").select("a.page-last").text()
        val regex = Regex("\\((\\d+)\\)$")
        val matchResult = regex.find(input)
        val number = matchResult?.groups?.get(1)?.value
        hasNextPage = page.toInt() != number?.toInt()
    }

    feedResponseListener.onSuccess (
        data = FeedResponseModel (
            response = "ok",
            data = mangaList,
            limit = mangaList.size,
            total = total.toInt(),
            page = page.toInt(),
            hasNextPage = hasNextPage
        )
    )
}