package com.flipperdevices.faphub.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FapCategory(
    val id: String,
    val name: String,
    val picUrl: String,
    val applicationCount: Int
) : Parcelable
