package com.flipperdevices.wearable.emulate.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry

interface WearEmulateApi : ComposableFeatureEntry {
    fun open(path: String): String
}
