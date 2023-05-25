package com.flipperdevices.faphub.dao.network.retrofit.model

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.dao.network.retrofit.utils.DateSerializer
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitApplicationShort(
    @SerialName("_id") val id: String,
    @SerialName("created_at")
    @Serializable(with = DateSerializer::class)
    val createdAt: LocalDateTime,
    @SerialName("updated_at")
    @Serializable(with = DateSerializer::class)
    val updatedAt: LocalDateTime,
    @SerialName("category_id") val categoryId: String,
    @SerialName("alias") val alias: String,
    @SerialName("name") val name: String,
    @SerialName("current_version") val currentVersion: RetrofitCurrentVersionShort,
) {
    fun toFapItemShort(category: FapCategory): FapItemShort {
        val fapItemVersion = FapItemVersion(
            id = currentVersion.id,
            version = SemVer.fromString(currentVersion.version)
                ?: error("Can't parse ${currentVersion.version}")
        )

        return FapItemShort(
            id = id,
            picUrl = currentVersion.iconUrl,
            description = currentVersion.description,
            name = name,
            category = category,
            screenshots = currentVersion.screenshots.toImmutableList(),
            applicationId = alias,
            upToDateVersion = fapItemVersion
        )
    }
}
