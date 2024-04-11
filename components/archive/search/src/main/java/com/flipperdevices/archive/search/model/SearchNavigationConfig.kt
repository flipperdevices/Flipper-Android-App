package com.flipperdevices.archive.search.model

import kotlinx.serialization.Serializable

@Serializable
sealed class SearchNavigationConfig {
    @Serializable
    data object Search : SearchNavigationConfig()
}
