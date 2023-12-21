package com.flipperdevices.archive.impl.model

import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
sealed class ArchiveNavigationConfig {
    @Serializable
    data object ArchiveObject : ArchiveNavigationConfig()

    @Serializable
    data class OpenKey(val flipperKeyPath: FlipperKeyPath) : ArchiveNavigationConfig()

    @Serializable
    data class OpenCategory(val categoryType: CategoryType) : ArchiveNavigationConfig()

    @Serializable
    data object OpenSearch : ArchiveNavigationConfig()
}
