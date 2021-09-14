package com.flipper.bridge.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {
    /**
     * Check if Bluetooth enabled in phone settings
     */
    fun isBluetoothEnabled(): Boolean {
        return BluetoothAdapter.getDefaultAdapter()?.isEnabled ?: false
    }

    /**
     * Return required permissions for current android version
     */
    fun getRequiredPermissions(): Array<String> {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * @return true if all permissions for connection via Bluetooth granted
     */
    fun isPermissionGranted(context: Context): Boolean {
        for (permissionName in getRequiredPermissions()) {
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
