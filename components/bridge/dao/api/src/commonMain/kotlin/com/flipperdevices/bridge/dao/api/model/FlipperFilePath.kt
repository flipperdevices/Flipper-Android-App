package com.flipperdevices.bridge.dao.api.model

import com.flipperdevices.core.kmpparcelize.KMPIgnoreOnParcel
import com.flipperdevices.core.kmpparcelize.KMPParcelable
import com.flipperdevices.core.kmpparcelize.KMPParcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

private const val FLIPPER_STORAGE_NAME = "/any/"

/**
 * Describe name and path for key.
 * Used as unique id for keys
 *
 * Never contains storage
 */
@Serializable
@KMPParcelize
data class FlipperFilePath(
    @SerialName("folder")
    val folder: String,
    @SerialName("name")
    val nameWithExtension: String // With extension
) : KMPParcelable, Comparable<FlipperFilePath> {
    @KMPIgnoreOnParcel
    val extension: String
        get() = nameWithExtension.substringAfterLast('.')

    @KMPIgnoreOnParcel
    val pathToKey: String by lazy {
        var path = File(folder, nameWithExtension).path
        if (path.firstOrNull() == File.separatorChar) {
            path = path.replaceFirst(File.separatorChar.toString(), "")
        }
        return@lazy path
    }

    @KMPIgnoreOnParcel
    val keyType: FlipperKeyType? by lazy {
        FlipperKeyType.getByExtension(extension)
    }

    @KMPIgnoreOnParcel
    val fileType: FlipperFileType by lazy {
        FlipperFileType.getByExtension(extension)
    }

    @KMPIgnoreOnParcel
    val nameWithoutExtension by lazy {
        nameWithExtension.substringAfterLast('/').substringBeforeLast(".")
    }

    fun getPathOnFlipper(): String = File(FLIPPER_STORAGE_NAME, pathToKey).path

    override fun toString() = pathToKey

    companion object {
        val DUMMY by lazy {
            FlipperFilePath(
                FlipperKeyType.NFC.flipperDir,
                "Test_Key.nfc"
            )
        }
    }

    fun copyWithChangedName(newNameWithoutExtension: String): FlipperFilePath {
        return FlipperFilePath(folder, "$newNameWithoutExtension.$extension")
    }

    override fun compareTo(other: FlipperFilePath): Int {
        return pathToKey.compareTo(other.pathToKey)
    }
}
