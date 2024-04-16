package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class MangaModel(
    val title: String?,
    val thumbnail: String?,
    val authors: List<AuthorModel>,
    val genres: List<GenreModel>,
    val status: String?,
    val updatedAt: String?,
    val description: String?,
    val view: String?,
    val rating: String?,
    val mangaUrl: String?,
    val chapterList: List<ChapterModel>
)