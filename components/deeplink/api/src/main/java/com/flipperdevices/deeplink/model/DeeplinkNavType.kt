package com.flipperdevices.deeplink.model

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.core.ktx.android.parcelable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DeeplinkNavType : NavType<Deeplink>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Deeplink? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): Deeplink {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: Deeplink) {
        bundle.putParcelable(key, value)
    }
}
