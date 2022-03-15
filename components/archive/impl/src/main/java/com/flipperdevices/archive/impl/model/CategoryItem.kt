package com.flipperdevices.archive.impl.model

import androidx.annotation.DrawableRes

data class CategoryItem(
    @DrawableRes val iconId: Int?,
    val title: String,
    val count: Int?
)
