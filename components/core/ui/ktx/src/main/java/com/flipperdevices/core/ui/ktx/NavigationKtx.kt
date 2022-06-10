package com.flipperdevices.core.ui.ktx

import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.cicerone.Router

val LocalRouter = staticCompositionLocalOf<Router> {
    noLocalProvidedFor("LocalRouter")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
