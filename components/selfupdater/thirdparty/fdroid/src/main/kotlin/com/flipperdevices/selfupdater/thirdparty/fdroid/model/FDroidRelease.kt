package com.flipperdevices.selfupdater.thirdparty.fdroid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FDroidReleases(
    @SerialName("packages")
    val packages: List<FDroidPackage>
)

@Serializable
data class FDroidPackage(
    @SerialName("versionName")
    val versionName: String,
    @SerialName("versionCode")
    val versionCode: Int,
)
