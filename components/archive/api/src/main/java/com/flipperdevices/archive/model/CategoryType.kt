package com.flipperdevices.archive.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
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
