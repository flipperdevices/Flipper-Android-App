package com.flipperdevices.archive.impl.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryItem(
    @DrawableRes val iconId: Int?,
    val title: String,
    val count: Int?
) : Parcelable
