package com.flipperdevices.faphub.category.impl.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.serialization.Serializable

@Serializable
sealed class FapCategoryNavigationConfig {
    @Serializable
    data class CategoryList(val fapCategory: FapCategory) : FapCategoryNavigationConfig()

    @Serializable
    data class FapScreen(val id: String) : FapCategoryNavigationConfig()

    @Serializable
    data object Search : FapCategoryNavigationConfig()
}
