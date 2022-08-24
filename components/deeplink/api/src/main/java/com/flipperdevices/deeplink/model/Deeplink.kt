package com.flipperdevices.deeplink.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import kotlinx.parcelize.Parcelize

@Parcelize
data class Deeplink constructor(
    val path: FlipperFilePath? = null,
    val content: DeeplinkContent? = null
) : Parcelable
