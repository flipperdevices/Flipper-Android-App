package com.flipperdevices.faphub.target.model

import com.flipperdevices.core.data.SemVer

sealed class FlipperTarget {
    object Unsupported : FlipperTarget()
    data class Received(
        val target: String,
        val sdk: SemVer
    ) : FlipperTarget()

    fun getTargetForServer(): String? {
        return when (this) {
            is Received -> target
            Unsupported -> null
        }
    }

    fun getApiForServer(): String? {
        return when (this) {
            is Received -> sdk.toString()
            Unsupported -> null
        }
    }
}
