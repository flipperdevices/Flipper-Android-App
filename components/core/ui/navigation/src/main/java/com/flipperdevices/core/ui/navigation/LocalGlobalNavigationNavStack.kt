package com.flipperdevices.core.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalGlobalNavigationNavStack = staticCompositionLocalOf<NavHostController> {
    noLocalProvidedFor("LocalGlobalNavigationNavStack")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
