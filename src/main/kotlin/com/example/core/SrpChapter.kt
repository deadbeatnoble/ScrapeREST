package com.example.core

import com.example.data.ChapterResponseModel
import com.example.data.PageModel
import org.jsoup.Jsoup

interface ChapterResponseListener {
    fun onSuccess(data: ChapterResponseModel)
    fun onFailure(message: String)
}

fun srpChapter(
    html: String,
    referrer: String,
    chapterResponseListener: ChapterResponseListener
) {

    val document = Jsoup.parse(html)
    val pageList = mutableListOf<PageModel>()

    document.getElementsByClass("container-chapter-reader").firstOrNull()?.let {
        it.select("img").forEach { pageImage ->
            pageImage?.let {
                pageList.add(
                    PageModel(
                        title = pageImage.attr("title"),
                        pageImageUrl = pageImage.attr("src")
                    )
                )
            } ?: pageList.add(
                PageModel(
                    title = null,
                    pageImageUrl = null
                )
            )
        }
    } ?: chapterResponseListener.onFailure(
        message = "Unable to PARSE the page from"
    )

    chapterResponseListener.onSuccess(
        data = ChapterResponseModel(
            response = "ok",
            data = pageList,
            referrer = referrer
        )
    )
}