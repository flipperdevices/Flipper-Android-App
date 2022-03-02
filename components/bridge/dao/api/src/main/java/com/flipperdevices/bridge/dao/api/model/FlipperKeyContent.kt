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
sealed class FlipperKeyContent : Parcelable {
    @Parcelize
    class RawData(private val bytes: ByteArray) : FlipperKeyContent() {
        override fun openStream() = ByteArrayInputStream(bytes)
        override fun length() = bytes.size.toLong()
    }

    @Parcelize
    data class InternalFile(val file: File) : FlipperKeyContent() {
        override fun openStream() = FileInputStream(file)
        override fun length() = file.length()
    }

    abstract fun openStream(): InputStream

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
