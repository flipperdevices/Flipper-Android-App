package com.flipperdevices.deeplink.model

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.parcelize.Parcelize

@Parcelize
data class Deeplink constructor(
    val path: FlipperKeyPath? = null,
    val content: DeeplinkContent? = null
) : Parcelable
