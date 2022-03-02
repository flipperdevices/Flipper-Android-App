package com.flipperdevices.core.ktx.android

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Vibrator.vibrateCompat(milliseconds: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(
            VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        // deprecated in API 26
        @Suppress("DEPRECATION")
        vibrate(milliseconds)
    }
}
