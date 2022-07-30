package com.flipperdevices.filemanager.impl.api

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DeeplinkContentType : NavType<DeeplinkContent>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): DeeplinkContent? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): DeeplinkContent {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: DeeplinkContent) {
        bundle.putParcelable(key, value)
    }
}
