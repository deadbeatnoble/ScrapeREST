package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class PageModel(
    val title: String?,
    val pageImageUrl: String?
)
