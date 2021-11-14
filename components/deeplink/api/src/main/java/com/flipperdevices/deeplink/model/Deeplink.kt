package com.flipperdevices.deeplink.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Deeplink(
    val content: DeeplinkContent? = null
) : Parcelable
