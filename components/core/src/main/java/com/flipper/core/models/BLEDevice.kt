package com.flipper.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BLEDevice(
    val id: String,
    val name: String
) : Parcelable

