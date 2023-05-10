package com.flipperdevices.faphub.dao.network.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationShort(
    @SerialName("_id") val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("alias") val alias: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("screenshots") val screenshots: List<String>,
) {
    companion object {
        fun ApplicationShort.toFapItemShort(category: FapCategory): FapItemShort {
            val picUrl = MockConstants.getMockItem().picUrl

            return FapItemShort(
                id = id,
                picUrl = picUrl,
                description = description,
                name = name,
                category = category,
            )
        }
    }
}
