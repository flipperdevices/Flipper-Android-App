package com.flipperdevices.archive.model

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed class CategoryType {
    @Serializable
    data class ByFileType(
        val fileType: FlipperKeyType
    ) : CategoryType()

    @Serializable
    data object Deleted : CategoryType()
}
