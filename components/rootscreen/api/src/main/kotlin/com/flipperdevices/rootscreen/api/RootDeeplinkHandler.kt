package com.flipperdevices.rootscreen.api

import androidx.compose.runtime.staticCompositionLocalOf
import com.flipperdevices.deeplink.model.Deeplink

val LocalDeeplinkHandler = staticCompositionLocalOf<RootDeeplinkHandler> {
    error("CompositionLocal LocalDeeplinkHandler not present")
}

interface RootDeeplinkHandler {
    fun handleDeeplink(deeplink: Deeplink)
}
