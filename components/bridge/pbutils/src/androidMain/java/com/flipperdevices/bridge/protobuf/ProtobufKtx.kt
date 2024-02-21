package com.flipperdevices.bridge.protobuf

import com.google.protobuf.GeneratedMessageLite
import java.io.ByteArrayOutputStream

fun GeneratedMessageLite<*, *>.toDelimitedBytes(): ByteArray {
    return ByteArrayOutputStream().use { os ->
        this.writeDelimitedTo(os)
        return@use os.toByteArray()
    }
}
