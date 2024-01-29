package com.flipperdevices.main.impl.model

import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.serialization.Serializable

@Serializable
sealed class FapHubNavigationConfig {
    @Serializable
    data object Main : FapHubNavigationConfig()

    @Serializable
    data class FapScreen(val id: String) : FapHubNavigationConfig()

    @Serializable
    data object Search : FapHubNavigationConfig()

    @Serializable
    data class Category(val fapCategory: FapCategory) : FapHubNavigationConfig()
}
