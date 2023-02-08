package com.flipperdevices.updater.screen.model.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UpdaterRequestType : NavType<UpdateRequest>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): UpdateRequest? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): UpdateRequest {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: UpdateRequest) {
        bundle.putParcelable(key, value)
    }
}
