package com.flipperdevices.updater.downloader.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.TaggedLogger
import com.flipperdevices.core.log.verbose
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class KtorModule {
    @Provides
    @Singleton
    fun providerKtorClient(): HttpClient {
        val ktorTimber = TaggedLogger("Ktor")

        return HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    }
                )
            }

            if (BuildConfig.INTERNAL) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            ktorTimber.verbose { message }
                        }
                    }
                    level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
