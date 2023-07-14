package com.flipperdevices.deeplink.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class Deeplink : Parcelable {
    @Parcelize
    @Serializable
    data class ExternalContent(
        val content: DeeplinkContent? = null
    ) : Deeplink()

    @Parcelize
    @Serializable
    data class FlipperKey(
        val path: FlipperFilePath,
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
    ) : Deeplink()

    @Parcelize
    @Serializable
    data class Fap(
        val appId: String,
    ) : Deeplink()
}
