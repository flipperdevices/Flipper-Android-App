package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

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

    data class InternalFile(val file: File) : FlipperKeyContent() {
        override fun stream() = FileInputStream(file)
        override fun length() = file.length()
    }

    abstract fun stream(): InputStream

    abstract fun length(): Long?
}

@Parcelize
data class FlipperFileFormat(
    val orderedDict: List<Pair<String, String>>
) : FlipperKeyContent(), Parcelable {
    @IgnoredOnParcel
    private val fileContentLazy by lazy {
        generateFileContent()
    }

    companion object {
        fun fromFileContent(fileContent: String): FlipperFileFormat {
            val pairs = fileContent.split("\n")
                .filterNot { it.startsWith("#") }
                .map {
                    it.substringBefore(":").trim() to
                        it.substringAfter(":").trim()
                }

            return FlipperFileFormat(pairs)
        }
    }

    override fun stream() = ByteArrayInputStream(fileContentLazy.toByteArray())
    override fun length() = fileContentLazy.toByteArray().size.toLong()

    private fun generateFileContent(): String {
        val sb = StringBuilder()
        orderedDict.forEach { line ->
            sb.append(line.first).append(": ").append(line.second).append('\n')
        }
        return sb.toString()
    }
}
