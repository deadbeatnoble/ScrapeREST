package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class GenreModel(
    val name: String?,
    val url: String?
)
