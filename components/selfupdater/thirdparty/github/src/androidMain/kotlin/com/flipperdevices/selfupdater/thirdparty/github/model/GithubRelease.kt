package com.flipperdevices.selfupdater.thirdparty.github.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val GMS_PREFIX = "flipper-zero-gms-"
private const val NO_GMS_PREFIX = "flipper-zero-nogms-"
private const val RELEASE_SUFFIX = "-release"
private const val APK_EXTENSION = ".apk"

@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("assets")
    val assets: List<GithubAsset>,
    @SerialName("name")
    val name: String,
    @SerialName("prerelease")
    val preRelease: Boolean
) {
    fun getDownloadUrl(isGooglePlayEnable: Boolean): String? {
        val prefix = if (isGooglePlayEnable) GMS_PREFIX else NO_GMS_PREFIX
        return assets
            .filter { it.name.endsWith(APK_EXTENSION) }
            .firstOrNull { it.name.contains(prefix) }
            ?.browserDownloadUrl
    }

    fun getVersion(isDev: Boolean): String {
        return if (isDev) {
            tagName
        } else {
            tagName.removeSuffix(RELEASE_SUFFIX)
        }
    }
}

@Serializable
data class GithubAsset(
    @SerialName("name")
    val name: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String
)
