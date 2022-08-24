package com.flipperdevices.archive.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.parcelize.Parcelize

sealed class CategoryType : Parcelable {
    @Parcelize
    data class ByFileType(
        val fileType: FlipperKeyType
    ) : CategoryType()

    @Parcelize
    object Deleted : CategoryType()
}
