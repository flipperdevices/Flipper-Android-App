package com.flipperdevices.selfupdater.source.github.parser

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.selfupdater.source.github.model.GithubRelease
import com.flipperdevices.selfupdater.source.github.model.GithubUpdate
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

private const val GITHUB_API_ALL_RELEASES =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases"
private const val GITHUB_API_LAST_RELEASE =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases/latest"

interface GithubParser {
    suspend fun getLastRelease(): GithubUpdate?

    fun isParserValid(): Boolean
}

@ContributesMultibinding(AppGraph::class, GithubParser::class)
class GithubDevParserImpl @Inject constructor(
    private val client: HttpClient,
    private val applicationParams: ApplicationParams
) : GithubParser {
    override suspend fun getLastRelease(): GithubUpdate? {
        val response = client.get(
            urlString = GITHUB_API_ALL_RELEASES
        ).body<List<GithubRelease>>()

        val dev = response.firstOrNull { it.isDev() } ?: return null
        val downloadUrl = dev.getDownloadUrl(applicationParams.isGooglePlayEnable) ?: return null

        return GithubUpdate(
            version = dev.tagName,
            downloadUrl = downloadUrl,
            name = dev.name
        )
    }

    override fun isParserValid(): Boolean {
        return applicationParams.isReleaseBuild().not()
    }
}

@ContributesMultibinding(AppGraph::class, GithubParser::class)
class GithubReleaseParserImpl @Inject constructor(
    private val client: HttpClient,
    private val applicationParams: ApplicationParams
) : GithubParser {
    override suspend fun getLastRelease(): GithubUpdate? {
        val release = client.get(
            urlString = GITHUB_API_LAST_RELEASE
        ).body<GithubRelease>()
        val downloadUrl = release.getDownloadUrl(applicationParams.isGooglePlayEnable) ?: return null

        return GithubUpdate(
            version = release.tagName,
            downloadUrl = downloadUrl,
            name = release.name
        )
    }

    override fun isParserValid(): Boolean {
        return applicationParams.isReleaseBuild()
    }
}

