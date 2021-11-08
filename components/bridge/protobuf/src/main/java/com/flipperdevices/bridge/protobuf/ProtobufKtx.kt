package com.flipperdevices.bridge.protobuf

import com.flipperdevices.protobuf.Flipper
import java.io.ByteArrayOutputStream

fun Flipper.Main.toDelimitedBytes(): ByteArray {
    return ByteArrayOutputStream().use { os ->
        this.writeDelimitedTo(os)
        return@use os.toByteArray()
    }
}
