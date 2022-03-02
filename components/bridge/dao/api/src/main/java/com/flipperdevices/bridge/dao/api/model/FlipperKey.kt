package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The most complete description of the key
 */
@Parcelize
data class FlipperKey(
    val path: FlipperKeyPath,
    val keyContent: FlipperKeyContent,
    val notes: String? = null
) : Parcelable
