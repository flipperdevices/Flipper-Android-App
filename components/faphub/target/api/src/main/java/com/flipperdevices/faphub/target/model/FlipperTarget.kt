package com.flipperdevices.faphub.target.model

import com.flipperdevices.core.data.SemVer

sealed class FlipperTarget {
    object Retrieving : FlipperTarget()
    object Unsupported : FlipperTarget()
    data class Received(
        val target: String,
        val sdk: SemVer
    ) : FlipperTarget()
}
