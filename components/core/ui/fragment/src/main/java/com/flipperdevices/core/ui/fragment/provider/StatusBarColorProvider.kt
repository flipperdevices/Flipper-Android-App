package com.flipperdevices.core.ui.fragment.provider

import androidx.annotation.ColorRes

interface StatusBarColorProvider {
    @ColorRes
    fun getStatusBarColor(): Int?
}
