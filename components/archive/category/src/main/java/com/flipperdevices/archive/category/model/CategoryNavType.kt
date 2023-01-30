package com.flipperdevices.archive.category.model

import android.os.Bundle
import androidx.navigation.NavType
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.ktx.android.parcelable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CategoryNavType : NavType<CategoryType>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): CategoryType? {
        return bundle.parcelable(key)
    }

    override fun parseValue(value: String): CategoryType {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: CategoryType) {
        bundle.putParcelable(key, value)
    }
}
