package com.flipperdevices.core.test

import androidx.test.platform.app.InstrumentationRegistry

fun readTestAsset(path: String): ByteArray {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    return context.resources.assets.open(path).use { it.readBytes() }
}

fun readTestAssetString(path: String): String {
    return String(readTestAsset(path))
}
