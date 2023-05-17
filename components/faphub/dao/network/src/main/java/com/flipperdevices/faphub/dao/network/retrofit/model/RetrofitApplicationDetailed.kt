package com.flipperdevices.faphub.dao.network.retrofit.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.network.model.ApplicationCurrentVersion
import com.flipperdevices.faphub.dao.network.model.ApplicationCurrentVersion.Companion.toFapMetaInformation
import com.flipperdevices.faphub.dao.network.model.ApplicationCurrentVersionLinks.Companion.toFapDeveloperInformation
import com.flipperdevices.faphub.dao.network.model.ApplicationVersionShort
import com.flipperdevices.faphub.dao.network.model.MockConstants.getMockItem
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitApplicationDetailed(
    @SerialName("_id") val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("alias") val alias: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("changelog") val changelog: String? = null,
    @SerialName("current_version") val currentVersion: ApplicationCurrentVersion? = null,
    @SerialName("versions") val versions: List<ApplicationVersionShort> = listOf(),
) {
    fun toFapItem(category: FapCategory): FapItem {
        val picUrl = getMockItem().picUrl

        val developerInformation = currentVersion
            ?.links
            ?.first()
            ?.toFapDeveloperInformation()
            ?: getMockItem().fapDeveloperInformation
        val metaInformation = currentVersion

            ?.toFapMetaInformation()
            ?: getMockItem().metaInformation

        val changelog = changelog ?: getMockItem().changelog

        return FapItem(
            id = id,
            screenshots = screenshots.toImmutableList(),
            description = description,
            name = name,
            changelog = changelog,
            category = category,
            picUrl = picUrl,
            metaInformation = metaInformation,
            fapDeveloperInformation = developerInformation
        )
    }
}
