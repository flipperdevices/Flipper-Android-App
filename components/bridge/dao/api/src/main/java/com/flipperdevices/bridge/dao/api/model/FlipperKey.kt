package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The most complete description of the key
 */
@Parcelize
data class FlipperKey(
    val mainFile: FlipperFile,
    val additionalFiles: List<FlipperFile> = emptyList(),
    val notes: String? = null,
    val synchronized: Boolean,
    val deleted: Boolean
) : Parcelable {
    val flipperKeyType: FlipperKeyType?
        get() = mainFile.path.keyType
    val path: FlipperFilePath
        get() = mainFile.path
    val keyContent: FlipperKeyContent
        get() = mainFile.content

    fun getKeyPath() = FlipperKeyPath(mainFile.path, deleted)
}

@Parcelize
data class FlipperKeyPath(
    val path: FlipperFilePath,
    val deleted: Boolean
) : Parcelable

@Parcelize
data class FlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent
) : Parcelable
