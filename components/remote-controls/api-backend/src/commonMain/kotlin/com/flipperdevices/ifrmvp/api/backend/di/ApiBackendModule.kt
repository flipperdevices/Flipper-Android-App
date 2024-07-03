package com.flipperdevices.ifrmvp.api.backend.di

import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.api.backend.ApiBackendImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * TODO ONLY FOR PREVIEW PURPOSES
 */
interface ApiBackendModule {
    val json: Json
    val httpClient: HttpClient
    val apiBackend: ApiBackend

    class Default(
        override val json: Json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        },
        override val httpClient: HttpClient = HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    ) : ApiBackendModule {

        override val apiBackend: ApiBackend by lazy {
            ApiBackendImpl(
                httpClient = httpClient,
                backendUrlHost = "192.168.0.100:8080",

                )
        }
    }
}
