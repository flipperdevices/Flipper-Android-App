package com.flipperdevices.archive.impl.model

import androidx.annotation.DrawableRes
import com.flipperdevices.archive.model.CategoryType

data class CategoryItem(
    @DrawableRes val iconId: Int?,
    val title: String,
    val count: Int?,
    val categoryType: CategoryType
)
