package com.flipperdevices.rootscreen.api

import androidx.compose.runtime.staticCompositionLocalOf
import com.flipperdevices.rootscreen.model.RootScreenConfig

val LocalRootNavigation = staticCompositionLocalOf<RootNavigationInterface> {
    error("CompositionLocal LocalRootComponent not present")
}

interface RootNavigationInterface {
    fun push(config: RootScreenConfig)
}
