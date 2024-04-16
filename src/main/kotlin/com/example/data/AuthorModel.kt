package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthorModel(
    val name: String?,
    val url: String?
)
