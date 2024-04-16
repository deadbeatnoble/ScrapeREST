package com.example

import com.example.plugins.*
import com.example.util.Document
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/popular").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(Document.feedDoc, bodyAsText())
        }
    }
}
