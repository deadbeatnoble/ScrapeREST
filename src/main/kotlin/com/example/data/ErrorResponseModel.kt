package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseModel(
    val response: String,
    val error: String
)
