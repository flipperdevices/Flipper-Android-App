package com.flipperdevices.bridge.synchronization.impl.model

import java.io.ByteArrayInputStream
import java.io.InputStream

sealed class FlipperKeyContent {
    class RawData(private val bytes: ByteArray) : FlipperKeyContent() {
        override fun stream() = ByteArrayInputStream(bytes)
        override fun length() = bytes.size.toLong()
    }

    abstract fun stream(): InputStream

    abstract fun length(): Long?
}
