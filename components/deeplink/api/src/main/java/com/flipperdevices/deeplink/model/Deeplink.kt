package com.flipperdevices.deeplink.model

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.parcelize.Parcelize
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

    fun buildIntent(): Intent {
        val deeplinkSerialized = Uri.encode(Json.encodeToString(this))
        return Intent().apply {
            data = Uri.parse("${DeeplinkConstants.SCHEMA}${buildDeeplinkKey(this@Deeplink)}/$deeplinkSerialized")
        }
    }

    companion object {
        fun buildDeeplinkKey(deeplink: Deeplink): String {
            return when (deeplink) {
                is FlipperKey -> DeeplinkConstants.FLIPPER_KEY
                is WidgetOptions -> DeeplinkConstants.WIDGET_OPTIONS
                is OpenKey -> DeeplinkConstants.OPEN_KEY
                is WebUpdate -> DeeplinkConstants.WEB_UPDATE
            }
        }

        fun buildDeeplinkPattern(deeplinkKey: String): String {
            return "${DeeplinkConstants.SCHEMA}$deeplinkKey/{${DeeplinkConstants.KEY}}"
        }
    }
}
