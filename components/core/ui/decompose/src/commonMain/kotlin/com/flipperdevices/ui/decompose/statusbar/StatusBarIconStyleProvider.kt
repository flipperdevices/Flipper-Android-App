package com.flipperdevices.ui.decompose.statusbar

internal interface StatusBarIconStyleProvider {
    fun isStatusBarIconLight(systemIsDark: Boolean): Boolean
}
