package com.flipperdevices.faphub.dao.network.model

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapMetaInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCurrentVersion(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("size") val size: Int,
    @SerialName("api") val api: String,
    @SerialName("links") val links: List<ApplicationCurrentVersionLinks>,
) {
    companion object {
        fun ApplicationCurrentVersion.toFapMetaInformation(): FapMetaInformation {
            val apiVersion = SemVer.fromString(api) ?: error("Invalid api version")
            val version = SemVer.fromString(version) ?: error("Invalid version")

            return FapMetaInformation(
                apiVersion = apiVersion,
                sizeBytes = size.toLong(),
                version = version
            )
        }
    }
}
