package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Describes the contents of the key.
 * May be a stream, a file, a link, or bytes.
 * Do not limit your support to only one type of content.
 */
@Serializable
sealed class FlipperKeyContent : Parcelable {
    @Parcelize
    data class RawData(val bytes: ByteArray) : FlipperKeyContent() {
        override fun openStream() = ByteArrayInputStream(bytes)
        override fun length() = bytes.size.toLong()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RawData

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    @Parcelize
    @Serializable
    data class InternalFile(val path: String) : FlipperKeyContent() {

        @IgnoredOnParcel
        @Transient
        private val file = File(path)

        override fun openStream(): InputStream {
            return if (file.exists()) {
                FileInputStream(file)
            } else {
                ByteArray(0).inputStream()
            }
        }

        override fun length() = file.length()
    }

    abstract fun openStream(): InputStream

    abstract fun length(): Long?
}

@Parcelize
@Serializable
data class FlipperFileFormat(
    val orderedDict: List<Pair<String, String>>
) : FlipperKeyContent(), Parcelable {
    @IgnoredOnParcel
    private val fileContentLazy by lazy {
        generateFileContent()
    }

    companion object {
        fun fromFlipperContent(flipperKeyContent: FlipperKeyContent): FlipperFileFormat {
            if (flipperKeyContent is FlipperFileFormat) {
                return flipperKeyContent
            }

            val fileContent = flipperKeyContent.openStream().use {
                String(it.readBytes())
            }

            return fromFileContent(fileContent)
        }

        fun fromFileContent(fileContent: String): FlipperFileFormat {
            val pairs = fileContent.split("\n\r")
                .asSequence()
                .map { it.split("\n") }.flatten()
                .map { it.split("\r") }.flatten()
                .filterNot { it.startsWith("#") }
                .map {
                    it.substringBefore(":").trim() to
                        it.substringAfter(":").trim()
                }.filterNot { it.first.isBlank() || it.second.isBlank() }
                .toList()

            return FlipperFileFormat(pairs)
        }
    }

    override fun openStream() = ByteArrayInputStream(fileContentLazy.toByteArray())
    override fun length() = fileContentLazy.toByteArray().size.toLong()

    private fun generateFileContent(): String {
        val sb = StringBuilder()
        orderedDict.forEach { line ->
            sb.append(line.first).append(": ").append(line.second).append('\n')
        }
        return sb.toString()
    }
}
