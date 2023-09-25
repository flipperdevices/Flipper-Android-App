package com.flipperdevices.faphub.dao.api.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Stable
data class FapCategory(
    val id: String,
    val name: String,
    val picUrl: String,
    val applicationCount: Int,
    @ColorInt val color: Int?
) : Parcelable
