package com.flipperdevices.selfupdater.source.github.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val DEV = "dev"

private const val GMS_PREFIX = "flipper-zero-gms-"
private const val NO_GMS_PREFIX = "flipper-zero-nogms-"

@Serializable
data class GithubRelease(
    @SerialName("target_commitish")
    val targetCommitish: String,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("assets")
    val assets: List<GithubAsset>,
    @SerialName("name")
    val name: String
) {
    fun isDev(): Boolean = targetCommitish == DEV
    fun getDownloadUrl(isGooglePlayEnable: Boolean): String? {
        val prefix = if (isGooglePlayEnable) GMS_PREFIX else NO_GMS_PREFIX
        return assets.firstOrNull { it.name.contains(prefix) }?.browserDownloadUrl
    }
}

@Serializable
data class GithubAsset(
    @SerialName("name")
    val name: String,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String
)
