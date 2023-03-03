package com.flipperdevices.deeplink.model

import android.net.Uri
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    @Parcelize
    @Serializable
    object OpenArchive: Deeplink(isInternal = true)

    @IgnoredOnParcel
    @delegate:Transient
    val serialization: String by lazy {
        Uri.encode(Json.encodeToString(this))
    }
}
