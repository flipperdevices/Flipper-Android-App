package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FlipperKeyCrypto(
    val fileId: String,
    val pathToKey: String,
    val cryptoKey: String
) : Parcelable
