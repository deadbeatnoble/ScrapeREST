package com.example.core

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay

suspend fun srpWholePage(
    baseUrl: String,
    responseListener: ResponseListener
) {
    delay(3000)
    try {
        val client = HttpClient(CIO)
        val doc = client.get(baseUrl)
        client.close()

        responseListener.onSuccess(data = doc.bodyAsText())
    } catch (e: Exception) {
        responseListener.onFailure(message = "Unable to ACCESS the page from $baseUrl: $e")
    }
}

interface ResponseListener {
    fun onSuccess(data: String)
    fun onFailure(message: String)
}