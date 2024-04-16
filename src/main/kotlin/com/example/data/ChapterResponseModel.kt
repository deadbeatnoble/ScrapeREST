package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class ChapterResponseModel(
    val response: String,
    val data: List<PageModel>,
    val referrer: String
)
