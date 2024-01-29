package com.flipperdevices.bridge.api.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object DeviceFeatureHelper {
    fun isCompanionFeatureAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.packageManager.hasSystemFeature(
                PackageManager.FEATURE_COMPANION_DEVICE_SETUP
            )
        }
        return false
    }
}
