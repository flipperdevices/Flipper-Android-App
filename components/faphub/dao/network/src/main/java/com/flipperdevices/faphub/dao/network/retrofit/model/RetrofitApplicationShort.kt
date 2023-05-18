package com.flipperdevices.faphub.dao.network.retrofit.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.network.model.MockConstants
import com.flipperdevices.faphub.dao.network.retrofit.utils.DateSerializer
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class RetrofitApplicationShort(
    @SerialName("_id") val id: String,
    @SerialName("created_at")
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    @SerialName("updated_at")
    @Serializable(with = DateSerializer::class)
    val updatedAt: Date,
    @SerialName("category_id") val categoryId: String,
    @SerialName("alias") val alias: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("screenshots") val screenshots: List<String>,
) {
    fun toFapItemShort(category: FapCategory): FapItemShort {
        val picUrl = MockConstants.getMockItem().picUrl

        return FapItemShort(
            id = id,
            picUrl = picUrl,
            description = description,
            name = name,
            category = category,
            screenshots = screenshots.toImmutableList()
        )
    }
}
