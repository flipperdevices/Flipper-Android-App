package com.flipperdevices.faphub.errors.api.throwable

import androidx.compose.runtime.Stable

@Stable
data class StableThrowable(
    val origin: Throwable,
)

fun Throwable.toStable() = StableThrowable(this)
