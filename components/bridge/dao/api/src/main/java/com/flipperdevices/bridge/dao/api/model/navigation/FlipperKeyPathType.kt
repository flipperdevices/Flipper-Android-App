package com.flipperdevices.bridge.dao.api.model.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.parcelable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FlipperKeyPathType : NavType<FlipperKeyPath>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): FlipperKeyPath? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): FlipperKeyPath {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: FlipperKeyPath) {
        bundle.putParcelable(key, value)
    }
}
