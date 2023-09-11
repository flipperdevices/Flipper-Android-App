package com.flipperdevices.selfupdater.thirdparty.github.parser

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.selfupdater.thirdparty.api.SelfUpdate
import com.flipperdevices.selfupdater.thirdparty.api.SelfUpdateParserApi
import com.flipperdevices.selfupdater.thirdparty.github.BuildConfig
import com.flipperdevices.selfupdater.thirdparty.github.model.GithubRelease
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

private const val GITHUB_API_ALL_RELEASES =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases"
private const val GITHUB_API_LAST_RELEASE =
    "https://api.github.com/repos/flipperdevices/Flipper-Android-App/releases/latest"

private const val DEV_BUILD_TYPE = "internal"

@ContributesBinding(AppGraph::class, SelfUpdateParserApi::class)
class GithubParser @Inject constructor(
    private val client: HttpClient
) : SelfUpdateParserApi, LogTagProvider {

    override val TAG: String = "GithubParser"

    override fun getName(): String = "Github. Google Feature: ${isGooglePlayEnable()}"

    override suspend fun getLastUpdate(): SelfUpdate? {
        val isDev = isDev()

        val update = if (isDev) {
            parseDevUpdate()
        } else {
            parseReleaseUpdate()
        }

        info { "Chooser update application $update" }

        val downloadUrl = update
            ?.getDownloadUrl(isGooglePlayEnable = isGooglePlayEnable())
            ?: return null

        info { "Download url for update application $downloadUrl" }

        return SelfUpdate(
            version = update.getVersion(isDev),
            downloadUrl = downloadUrl,
            name = update.name
        )
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

        return response.firstOrNull { it.preRelease }
    }

    private fun isGooglePlayEnable() = BuildConfig.IS_GOOGLE_FEATURE_AVAILABLE

    private fun isDev(): Boolean {
        return BuildConfig.BUILD_TYPE == DEV_BUILD_TYPE
    }
}
