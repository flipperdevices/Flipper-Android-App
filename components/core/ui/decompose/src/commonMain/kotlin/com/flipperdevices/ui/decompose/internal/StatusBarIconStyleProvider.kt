package com.flipperdevices.ui.decompose.internal

internal interface StatusBarIconStyleProvider {
    fun isStatusBarIconLight(systemIsDark: Boolean): Boolean
}
