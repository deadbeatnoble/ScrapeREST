package com.example.core

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun srpWholePage(baseUrl: String): String {

    //manga detail -> https://chapmanganato.to/manga-ax951880
    //list of popular manga ->  https://manganato.com/genre-all?type=topview

    val client = HttpClient(CIO)
    val doc = client.get(baseUrl)
    client.close()

    return doc.bodyAsText()
}