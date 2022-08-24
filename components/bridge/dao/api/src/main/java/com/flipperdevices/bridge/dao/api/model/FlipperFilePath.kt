package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val FLIPPER_STORAGE_NAME = "/any/"

/**
 * Describe name and path for key.
 * Used as unique id for keys
 *
 * Never contains storage
 */
@Serializable
@Parcelize
data class FlipperFilePath constructor(
    @SerialName("folder")
    val folder: String,
    @SerialName("name")
    val nameWithExtension: String // With extension
) : Parcelable, Comparable<FlipperFilePath> {
    @IgnoredOnParcel
    val pathToKey: String by lazy { File(folder, nameWithExtension).path }

    @IgnoredOnParcel
    val keyType: FlipperKeyType? by lazy {
        FlipperKeyType.getByExtension(
            nameWithExtension.substringAfterLast('.')
        )
    }

    /**
     * NFC shadow file
     */
    val isShadowFile: Boolean
        get() = nameWithExtension.substringAfterLast('.') == SHADOW_FILE_EXTENSION

    @IgnoredOnParcel
    val nameWithoutExtension by lazy {
        nameWithExtension.substringAfterLast('/').substringBeforeLast(".")
    }

    fun getPathOnFlipper() = File(FLIPPER_STORAGE_NAME, pathToKey).path

    override fun toString() = pathToKey

    companion object {
        val DUMMY by lazy {
            FlipperFilePath(
                FlipperKeyType.NFC.flipperDir,
                "Test_Key.nfc"
            )
        }
    }

    override fun compareTo(other: FlipperFilePath): Int {
        return pathToKey.compareTo(other.pathToKey)
    }
}
