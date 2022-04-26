package com.flipperdevices.updater.downloader.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.TaggedTimber
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@ContributesTo(AppGraph::class)
class KtorModule {

    @Provides
    @Singleton
    fun providerKtorClient(): HttpClient {
        val ktorTimber = TaggedTimber("Ktor")

        return HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
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
                            ktorTimber.info { message }
                        }
                    }
                    level = LogLevel.INFO
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
