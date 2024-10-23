package com.flipperdevices.filemanager.listing.impl.composable.modal.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal val WindowInsets.Companion.zero: WindowInsets
    get() = object : WindowInsets {
        override fun getBottom(density: Density): Int = 0

        override fun getLeft(density: Density, layoutDirection: LayoutDirection): Int = 0

        override fun getRight(density: Density, layoutDirection: LayoutDirection): Int = 0

        override fun getTop(density: Density): Int = 0
    }
