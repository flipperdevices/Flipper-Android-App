package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Describe name and path for key.
 * Used as unique id for keys
 *
 * Never contains storage
 */
@Serializable
@Parcelize
data class FlipperKeyPath constructor(
    val folder: String,
    val name: String, // With extension
    val deleted: Boolean
) : Parcelable, Comparable<FlipperKeyPath> {
    @IgnoredOnParcel
    val pathToKey: String by lazy { File(folder, name).path }

    @IgnoredOnParcel
    val fileType: FlipperFileType? by lazy {
        FlipperFileType.getByExtension(
            name.substringAfterLast(
                '.'
            )
        )
    }

    @IgnoredOnParcel
    val nameWithoutExtension by lazy {
        name.substringAfterLast('/').substringBeforeLast(".")
    }

    override fun toString() = pathToKey

    companion object {
        val DUMMY by lazy {
            FlipperKeyPath(
                FlipperFileType.NFC.flipperDir, "Test_Key.nfc",
                deleted = false
            )
        }
    }

    override fun compareTo(other: FlipperKeyPath): Int {
        return pathToKey.compareTo(other.pathToKey)
    }
}
