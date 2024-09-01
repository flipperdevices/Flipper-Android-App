package com.flipperdevices.bridge.dao.api.model

import androidx.compose.runtime.Immutable
import com.flipperdevices.core.kmpparcelize.KMPParcelable
import com.flipperdevices.core.kmpparcelize.KMPParcelize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The most complete description of the key
 */
@Serializable
@Immutable
data class FlipperKey(
    val mainFile: FlipperFile,
    val additionalFiles: ImmutableList<FlipperFile> = persistentListOf(),
    val notes: String? = null,
    val synchronized: Boolean,
    val deleted: Boolean
) {
    val flipperKeyType: FlipperKeyType?
        get() = mainFile.path.keyType
    val path: FlipperFilePath
        get() = mainFile.path
    val keyContent: FlipperKeyContent
        get() = mainFile.content

    fun getKeyPath() = FlipperKeyPath(mainFile.path, deleted)
}

@Serializable
@Immutable
@KMPParcelize
data class FlipperKeyPath(
    @SerialName("path")
    val path: FlipperFilePath,
    @SerialName("deleted")
    val deleted: Boolean
) : KMPParcelable

@Serializable
data class FlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent
)
