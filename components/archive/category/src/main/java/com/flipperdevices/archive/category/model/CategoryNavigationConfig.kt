package com.flipperdevices.archive.category.model

import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class CategoryNavigationConfig {
    @Serializable
    data class Category(val categoryType: CategoryType) : CategoryNavigationConfig()
}
