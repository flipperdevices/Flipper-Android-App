package com.flipperdevices.firstpair.impl.fragments.permissions

import android.content.Context
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionEnableHelper(
    private val context: Context,
    private val listener: Listener,
    private val permissions: Array<String>
) : LogTagProvider {
    override val TAG = "PermissionEnableHelper"

    private val _state = MutableStateFlow(arrayOf<String>())
    fun state() = _state.asStateFlow()
    fun processPermissionActivityResult(
        permissionsGrantedMap: Map<String, @JvmSuppressWildcards Boolean>,
    ) {
        _state.update { arrayOf() }
        val notGrantedPermissions = permissionsGrantedMap.filterNot { it.value }.map { it.key }
        if (notGrantedPermissions.isEmpty()) {
            verbose { "User grant all permission (${permissions.contentToString()})" }
            listener.onPermissionGranted(permissions)
            return
        }
        warn { "User denied for permissions $notGrantedPermissions" }
        listener.onPermissionUserDenied(notGrantedPermissions.toTypedArray())
    }

    fun requestPermissions() {
        if (getUngrantedPermission().isEmpty()) {
            warn { "Request grant permissions ${permissions.contentToString()}, but it already granted" }
            // Already granted permissions
            listener.onPermissionGranted(permissions)
            return
        }

        verbose { "Request grant permissions ${permissions.contentToString()}" }
        _state.update { permissions }
    }

    fun getUngrantedPermission(): List<String> {
        return PermissionHelper.getUngrantedPermission(context, permissions)
    }

    interface Listener {
        fun onPermissionGranted(permissions: Array<String>)
        fun onPermissionUserDenied(permissions: Array<String>)
    }
}
