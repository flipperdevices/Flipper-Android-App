package com.flipperdevices.bottombar.impl.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipperdevices.bottombar.impl.R

/**
 * Warning: this class is processed by Proguard
 */
enum class FlipperBottomTab(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
) {
    DEVICE(R.drawable.ic_device, R.string.bar_title_device),
    STORAGE(R.drawable.ic_storage, R.string.bar_title_storage),
    SETTINGS(R.drawable.ic_settings, R.string.bar_title_settings)
}
