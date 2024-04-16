package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class ChapterModel(
    val title: String?,
    val view: String?,
    val uploadedAt: String?,
    val chapterUrl: String?
)