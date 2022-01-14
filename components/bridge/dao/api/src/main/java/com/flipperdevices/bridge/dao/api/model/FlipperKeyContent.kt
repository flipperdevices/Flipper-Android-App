package com.flipperdevices.bridge.dao.api.model

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Describes the contents of the key.
 * May be a stream, a file, a link, or bytes.
 * Do not limit your support to only one type of content.
 */
sealed class FlipperKeyContent {
    class RawData(private val bytes: ByteArray) : FlipperKeyContent() {
        override fun stream() = ByteArrayInputStream(bytes)
        override fun length() = bytes.size.toLong()
    }

    class InternalFile(val file: File) : FlipperKeyContent() {
        override fun stream() = FileInputStream(file)
        override fun length() = file.length()
    }

    abstract fun stream(): InputStream

    abstract fun length(): Long?
}
