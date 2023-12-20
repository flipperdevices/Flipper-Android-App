package com.flipperdevices.archive.search.model

import com.flipperdevices.archive.api.SelectKeyPathListener
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class SearchNavigationConfig {
    @Serializable
    data class Search(
        val onItemSelected: SelectKeyPathListener?
    ) : SearchNavigationConfig()

    @Serializable
    data class OpenKey(
        val keyPath: FlipperKeyPath
    ) : SearchNavigationConfig()
}
