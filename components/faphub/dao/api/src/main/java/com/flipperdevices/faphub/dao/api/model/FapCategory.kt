package com.flipperdevices.faphub.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FapCategory(
    val name: String,
    val picUrl: String
) : Parcelable
