package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseModel(
    val response: String,
    val data: List<MangaModel>,
    val limit: Int,
    val total: Int,
    val page: Int,
    val hasNextPage: Boolean
)
