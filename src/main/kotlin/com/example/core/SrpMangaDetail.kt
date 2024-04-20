package com.example.core

import com.example.data.AuthorModel
import com.example.data.ChapterModel
import com.example.data.GenreModel
import com.example.data.MangaModel
import org.jsoup.Jsoup

interface MangaResponseListener {
    fun onSuccess(data: MangaModel)
    fun onFailure(message: String)
}

fun srpMangaDetail(
    html: String,
    mangaUrl: String,
    mangaResponseListener: MangaResponseListener
) {
    val document = Jsoup.parse(html)

    val authors = mutableListOf<AuthorModel>()
    var status: String? = null
    val genres = mutableListOf<GenreModel>()

    var updatedAt: String? = null
    var view: String? = null

    val chapterList = mutableListOf<ChapterModel>()

    document.getElementsByClass("variations-tableInfo").firstOrNull()?.select("tbody")?.firstOrNull()?.let { tableRows ->
        tableRows.children().forEach { tableRow ->
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
    } ?: mangaResponseListener.onFailure(
        message = "Unable to PARSE the page from"
    )


    document.getElementsByClass("story-info-right-extent").firstOrNull()?.let { pList ->
        pList.children().forEach { p ->
            when(p.getElementsByClass("stre-label").text().replace("\"", "").split(" ")[0]) {
                "Updated" -> updatedAt = p.getElementsByClass("stre-value").text() ?: null
                "View" -> view = p.getElementsByClass("stre-value").text() ?: null
            }
        }
    }

    document.getElementsByClass("panel-story-chapter-list").firstOrNull()?.let {
        it.select("ul.row-content-chapter").firstOrNull()?.let { list ->
            list.children().forEach { chapter ->
                chapterList.add(
                    ChapterModel(
                        title = chapter.select("a").text() ?: null,
                        view = chapter.select("span.chapter-view").text() ?: null,
                        uploadedAt = chapter.select("span.chapter-time").attr("title") ?: null,
                        chapterUrl = chapter.select("a").attr("href") ?: null
                    )
                )
            }
        } ?: chapterList.add(
            ChapterModel(
                title = null,
                view = null,
                uploadedAt = null,
                chapterUrl = null
            )
        )
    }


    mangaResponseListener.onSuccess(
        data = MangaModel(
            title = document.getElementsByClass("story-info-right").select("h1").text() ?: null,
            thumbnail = document.getElementsByClass("story-info-left").select("span.info-image").select("img").attr("src") ?: null,
            authors = authors,
            genres = genres,
            status = status,
            updatedAt = updatedAt,
            description = document.getElementsByClass("panel-story-info").select("div.panel-story-info-description").text().split(":", limit = 2)[1].trim(),
            view = view,
            rating = document.getElementById("rate_row_cmd")?.text()?.split(":")?.get(1)?.trim()?.split("/")?.get(0)?.trim(),
            mangaUrl = mangaUrl,
            chapterList = chapterList
        )
    )
}