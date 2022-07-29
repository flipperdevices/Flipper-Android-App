package com.flipperdevices.core.ui.navigation

import androidx.navigation.NamedNavArgument

// https://proandroiddev.com/navigating-through-multi-module-jetpack-compose-applications-6c9a31fa12b6
interface FeatureEntry {
    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()
}
