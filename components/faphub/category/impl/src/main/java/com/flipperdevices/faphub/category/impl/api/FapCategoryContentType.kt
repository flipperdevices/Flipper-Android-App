package com.flipperdevices.faphub.category.impl.api

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FapCategoryContentType : NavType<FapCategory>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): FapCategory? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): FapCategory {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: FapCategory) {
        bundle.putParcelable(key, value)
    }
}
