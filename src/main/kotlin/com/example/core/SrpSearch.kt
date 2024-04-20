package com.example.core

import com.example.data.AuthorModel
import com.example.data.FeedResponseModel
import com.example.data.MangaModel
import org.jsoup.Jsoup

fun srpSearch(
    html: String,
    feedResponseListener: FeedResponseListener
) {
    val document = Jsoup.parse(html)
    val mangaList = mutableListOf<MangaModel>()

    lateinit var total: String
    lateinit var page: String
    var hasNextPage: Boolean

    document.getElementsByClass("panel-search-story").firstOrNull()?.let { storyItems ->
        storyItems.children().forEach { storyItem ->
            val author = mutableListOf<AuthorModel>()

            storyItem.getElementsByClass("item-right").select("span.item-author").firstOrNull()?.let {
                it.text().split(",").let { authorNames ->
                    authorNames.forEach {  authorName ->
                        author.add(
                            AuthorModel(
                                name = authorName,
                                url = null
                            )
                        )
                    }
                }
            }

            mangaList.add(
                MangaModel(
                    title = storyItem.getElementsByClass("item-right").select("h3 a").text() ?: null,
                    thumbnail = storyItem.select("a img").attr("src") ?: null,
                    authors = author,
                    genres = emptyList(),
                    status = null,
                    updatedAt = storyItem.getElementsByClass("item-right").select("span.item-time").firstOrNull()?.text()?.split(":", limit = 2)?.get(1)?.trim(),
                    description = null,
                    view = storyItem.getElementsByClass("item-right").select("span.item-time")[1].text().split(":")[1].trim(),
                    rating = storyItem.select("a").select("em.item-rate").text() ?: null,
                    mangaUrl = storyItem.select("a").attr("href") ?: null,
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
        total = document.getElementsByClass("panel-page-number").select("div.group-qty").select("a").text().split(":")[1].trim().replace(",", "")
        page = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-blue")[1].text()

        val input = document.getElementsByClass("panel-page-number").select("div.group-page").select("a.page-last").text()
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
            total = total.toInt(),
            page = page.toInt(),
            hasNextPage = hasNextPage
        )
    )
}