package com.flipperdevices.selfupdater.thirdparty.fdroid.parser

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.thirdparty.api.SelfUpdate
import com.flipperdevices.selfupdater.thirdparty.api.SelfUpdateParserApi
import com.flipperdevices.selfupdater.thirdparty.fdroid.model.FDroidReleases
import com.squareup.anvil.annotations.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

private const val FDROID_API_ALL_RELEASES =
    "https://f-droid.org/api/v1/packages/com.flipperdevices.app"
private const val FDROID_API_DOWNLOAD_RELEASE_PREFIX =
    "https://f-droid.org/repo/com.flipperdevices.app_"

@ContributesBinding(AppGraph::class, SelfUpdateParserApi::class)
class FDroidParser @Inject constructor(
    private val client: HttpClient
) : SelfUpdateParserApi {
    override fun getName(): String = "FDroid"

    override suspend fun getLastUpdate(): SelfUpdate? {
        return runCatching {
            val releases = client.get(
                urlString = FDROID_API_ALL_RELEASES
            ).body<FDroidReleases>()

            val lastRelease = releases
                .packages
                .firstOrNull()
                ?: return null

            val code = lastRelease.versionCode
            val version = lastRelease.versionName

            val downloadUrl = "$FDROID_API_DOWNLOAD_RELEASE_PREFIX$code.apk"

            return SelfUpdate(
                version = version,
                downloadUrl = downloadUrl,
                name = "Flipper App $version",
            )
        }.getOrNull()
    }
}
