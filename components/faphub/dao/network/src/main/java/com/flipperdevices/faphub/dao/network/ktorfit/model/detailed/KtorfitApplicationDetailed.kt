package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapDeveloperInformation
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.dao.api.model.FapMetaInformation
import com.flipperdevices.faphub.dao.network.ktorfit.utils.DateSerializer
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitApplicationDetailed(
    @SerialName("_id") val id: String,
    @SerialName("created_at")
    @Serializable(with = DateSerializer::class)
    val createdAt: LocalDateTime,
    @SerialName("updated_at")
    @Serializable(with = DateSerializer::class)
    val updatedAt: LocalDateTime,
    @SerialName("category_id") val categoryId: String,
    @SerialName("alias") val alias: String,
    @SerialName("current_version") val currentVersion: KtorfitVersionDetailed,
) {
    fun toFapItem(category: FapCategory, target: FlipperTarget): FapItem {
        val picUrl = currentVersion.iconUrl

        val fapDeveloperInformation = FapDeveloperInformation(
            githubRepositoryLink = currentVersion.links.sourceCode.url,
            manifestRepositoryLink = currentVersion.links.manifestUrl
        )

        val metaInformation = FapMetaInformation(
            version = SemVer.fromString(currentVersion.version)
                ?: error("Failed parse ${currentVersion.version}"),
            sizeBytes = currentVersion.currentBuild.metadata?.sizeBytes
        )

        val fapItemVersion = FapItemVersion(
            id = currentVersion.id,
            version = SemVer.fromString(currentVersion.version)
                ?: error("Can't parse ${currentVersion.version}"),
            target = target,
            buildState = currentVersion.status.toFapBuildState(target),
            sdkApi = SemVer.fromString(currentVersion.currentBuild.sdk.api)
        )

        return FapItem(
            id = id,
            screenshots = currentVersion.screenshots.toImmutableList(),
            description = currentVersion.description,
            shortDescription = currentVersion.shortDescription,
            name = currentVersion.name,
            changelog = currentVersion.changelog,
            category = category,
            picUrl = picUrl,
            metaInformation = metaInformation,
            fapDeveloperInformation = fapDeveloperInformation,
            applicationAlias = alias,
            upToDateVersion = fapItemVersion
        )
    }
}
