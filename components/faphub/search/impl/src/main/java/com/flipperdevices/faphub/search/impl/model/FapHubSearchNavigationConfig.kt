package com.flipperdevices.faphub.search.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class FapHubSearchNavigationConfig {
    @Serializable
    data object SearchScreen : FapHubSearchNavigationConfig()

    @Serializable
    data class FapScreen(val id: String) : FapHubSearchNavigationConfig()
}
