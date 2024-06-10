package com.flipperdevices.main.impl.model

import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.dao.api.model.FapCategory
import kotlinx.serialization.Serializable

@Serializable
sealed class FapHubNavigationConfig {
    @Serializable
    data class Main(
        val deeplink: Deeplink.BottomBar.AppsTab.MainScreen?
    ) : FapHubNavigationConfig()

    @Serializable
    data class FapScreen(val id: String) : FapHubNavigationConfig()

    @Serializable
    data object Search : FapHubNavigationConfig()

    @Serializable
    data class Category(val fapCategory: FapCategory) : FapHubNavigationConfig()
}
