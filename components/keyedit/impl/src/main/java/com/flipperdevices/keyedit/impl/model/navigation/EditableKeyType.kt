package com.flipperdevices.keyedit.impl.model.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.keyedit.impl.model.EditableKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class EditableKeyType : NavType<EditableKey>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): EditableKey? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): EditableKey {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: EditableKey) {
        bundle.putParcelable(key, value)
    }
}
