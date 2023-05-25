package com.flipperdevices.faphub.target.impl.model

import com.flipperdevices.core.data.SemVer

sealed class FlipperSdkVersion {
    object InProgress : FlipperSdkVersion()
    object Unsupported : FlipperSdkVersion()
    object Error : FlipperSdkVersion()
    data class Received(val sdk: SemVer) : FlipperSdkVersion()
}
