package com.flipperdevices.archive.search.model

import com.flipperdevices.archive.api.SelectKeyPathListener
import kotlinx.serialization.Serializable

@Serializable
sealed class SearchNavigationConfig {
    @Serializable
    data class Search(
        val onItemSelected: SelectKeyPathListener?
    ) : SearchNavigationConfig()
}
