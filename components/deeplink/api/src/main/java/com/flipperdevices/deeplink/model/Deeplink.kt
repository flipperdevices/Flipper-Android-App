package com.flipperdevices.deeplink.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class Deeplink(
    val isInternal: Boolean = false
) : Parcelable {
    @Parcelize
    @Serializable
    data class FlipperKey(
        val path: FlipperFilePath? = null,
        val content: DeeplinkContent? = null
    ) : Deeplink()

    @Parcelize
    @Serializable
    data class WidgetOptions(
        val appWidgetId: Int
    ) : Deeplink()

    @Parcelize
    @Serializable
    data class OpenKey(
        val keyPath: FlipperKeyPath
    ) : Deeplink()

    @Parcelize
    @Serializable
    data class WebUpdate(
        val url: String,
        val name: String,
    ) : Deeplink(isInternal = true)
}
