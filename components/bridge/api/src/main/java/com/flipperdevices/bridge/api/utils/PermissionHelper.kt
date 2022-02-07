package com.flipperdevices.bridge.api.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {
    /**
     * Return required permissions for current android version
     */
    fun getRequiredPermissions(): Array<String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        }
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * @return true if all permissions granted
     */
    fun isPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        for (permissionName in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permissionName
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}
