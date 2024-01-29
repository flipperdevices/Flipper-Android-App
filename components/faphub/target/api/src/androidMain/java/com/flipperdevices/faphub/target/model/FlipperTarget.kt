package com.flipperdevices.faphub.target.model

import com.flipperdevices.core.data.SemVer

sealed class FlipperTarget {
    data object Unsupported : FlipperTarget()

    data object NotConnected : FlipperTarget()

    data class Received(
        val target: String,
        val sdk: SemVer
    ) : FlipperTarget()
}
