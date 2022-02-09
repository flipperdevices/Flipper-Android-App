package com.flipperdevices.firstpair.impl.fragments.permissions

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn

class PermissionEnableHelper(
    fragment: Fragment,
    private val context: Context,
    private val listener: Listener,
    private val permissions: Array<String>
) : LogTagProvider {
    override val TAG = "PermissionEnableHelper"

    private val requestPermissionsWithResult = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsGrantedMap ->
        val notGrantedPermissions = permissionsGrantedMap.filterNot { it.value }.map { it.key }
        if (notGrantedPermissions.isEmpty()) {
            verbose { "User grant all permission ($permissions)" }
            listener.onPermissionGranted(permissions)
            return@registerForActivityResult
        }
        warn { "User denied for permissions $notGrantedPermissions" }
        listener.onPermissionUserDenied(notGrantedPermissions.toTypedArray())
    }

    fun requestPermissions() {
        if (getUngrantedPermission().isEmpty()) {
            warn { "Request grant permissions $permissions, but it already granted" }
            // Already granted permissions
            listener.onPermissionGranted(permissions)
            return
        }

        verbose { "Request grant permissions $permissions" }
        requestPermissionsWithResult.launch(permissions)
    }

    fun getUngrantedPermission(): List<String> {
        return PermissionHelper.getUngrantedPermission(context, permissions)
    }

    interface Listener {
        fun onPermissionGranted(permissions: Array<String>)
        fun onPermissionUserDenied(permissions: Array<String>)
    }
}
