package com.example.plugins

import com.example.route.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        feedRoute()
        mangaRoute()
        searchRoute()
        advancedSearchRoute()
        chapterRoute()
        authorRoute()
        genreRoute()
    }
}
