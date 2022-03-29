package com.flipperdevices.core.ui.provider

import androidx.annotation.ColorRes

interface StatusBarColorProvider {
    @ColorRes
    fun getStatusBarColor(): Int?
}
