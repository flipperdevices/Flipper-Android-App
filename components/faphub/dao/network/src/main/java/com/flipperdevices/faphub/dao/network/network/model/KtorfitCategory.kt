package com.flipperdevices.faphub.dao.network.network.model

import android.graphics.Color
import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitCategory(
    @SerialName("_id") val id: String,
    @SerialName("priority") val priority: Int,
    @SerialName("name") val name: String,
    @SerialName("color") val color: String,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("applications") val applicationsCount: Int
) {
    fun toFapCategory(): FapCategory {
        return FapCategory(
            id = id,
            name = name,
            picUrl = iconUrl,
            applicationCount = applicationsCount,
            color = runCatching { Color.parseColor(color) }.getOrNull()
        )
    }
}
