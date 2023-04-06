package com.flipperdevices.selfupdater.source.github.parser

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.BuildConfig
import com.flipperdevices.selfupdater.source.github.model.GithubRelease
import com.flipperdevices.selfupdater.source.github.model.GithubUpdate
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

private const val GITHUB_API_ALL_RELEASES =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases"
private const val GITHUB_API_LAST_RELEASE =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases/latest"

private const val INTERNAL = "internal"

interface GithubParser {
    suspend fun getLastUpdate(): GithubUpdate?
}

@ContributesBinding(AppGraph::class, GithubParser::class)
class GithubParserImpl @Inject constructor(
    private val client: HttpClient
) : GithubParser {
    override suspend fun getLastUpdate(): GithubUpdate? {
        return runCatching {
            val update = if (isDev()) {
                parseDevUpdate()
            } else {
                parseReleaseUpdate()
            }

            val downloadUrl = update
                ?.getDownloadUrl(isGooglePlayEnable = isGooglePlayEnable())
                ?: return null

            return GithubUpdate(
                version = update.tagName,
                downloadUrl = downloadUrl,
                name = update.name
            )
        }.getOrNull()
    }

    private suspend fun parseReleaseUpdate(): GithubRelease {
        return client.get(
            urlString = GITHUB_API_LAST_RELEASE
        ).body()
    }

    private suspend fun parseDevUpdate(): GithubRelease? {
        val response = client.get(
            urlString = GITHUB_API_ALL_RELEASES
        ).body<List<GithubRelease>>()

        return response.firstOrNull { it.isDev() }
    }

    private fun isGooglePlayEnable(): Boolean {
        return System.getProperty("is_google_feature", "true") == "true"
    }

    private fun isDev(): Boolean {
        return BuildConfig.BUILD_TYPE == INTERNAL
    }
}
