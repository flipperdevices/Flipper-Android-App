package com.flipperdevices.core.ktx.android

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Vibrator.vibrateCompat(milliseconds: Long, ignoreVibration: Boolean) {
    if (ignoreVibration) {
        return
    }
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
