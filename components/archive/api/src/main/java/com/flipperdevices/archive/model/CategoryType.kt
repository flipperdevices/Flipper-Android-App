package com.flipperdevices.archive.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed class CategoryType : Parcelable {
    @Parcelize
    @Serializable
    data class ByFileType(
        val fileType: FlipperKeyType
    ) : CategoryType()

    @Parcelize
    @Serializable
    object Deleted : CategoryType()
}
