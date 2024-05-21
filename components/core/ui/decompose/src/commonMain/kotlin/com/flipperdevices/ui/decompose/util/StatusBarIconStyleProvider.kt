package com.flipperdevices.ui.decompose.util

internal interface StatusBarIconStyleProvider {
    fun isStatusBarIconLight(systemIsDark: Boolean): Boolean
}
