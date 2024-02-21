package com.flipperdevices.core.permission.api

typealias PermissionListener = (String, Boolean) -> Unit

interface PermissionRequestHandler {
    fun requestPermission(vararg permissions: String, listener: PermissionListener)
}
