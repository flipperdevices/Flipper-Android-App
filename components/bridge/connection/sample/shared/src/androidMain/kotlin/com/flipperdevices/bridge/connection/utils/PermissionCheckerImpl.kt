package com.flipperdevices.bridge.connection.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.flipperdevices.bridge.connection.screens.utils.PermissionChecker
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<PermissionChecker>())
class PermissionCheckerImpl @Inject constructor(
    private val context: Context
) : PermissionChecker {
    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }
}
