package com.flipperdevices.archive.impl.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.flipperdevices.archive.model.CategoryType
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryItem(
    @DrawableRes val iconId: Int?,
    val title: String,
    val count: Int?,
    val categoryType: CategoryType
) : Parcelable
