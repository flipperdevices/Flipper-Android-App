package com.flipperdevices.deeplink.model

import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
sealed class DeeplinkContent {
    @Serializable
    data class FFFContent(
        val filename: String,
        val flipperFileFormat: FlipperFileFormat
    ) : DeeplinkContent()

    @Serializable
    data class ExternalUri(
        val filename: String?,
        val size: Long?,
        val uriString: String
    ) : DeeplinkContent()

    @Serializable
    data class InternalStorageFile(
        val filePath: String
    ) : DeeplinkContent() {
        @Transient
        val file by lazy { File(filePath) }
    }

    @Serializable
    data class FFFCryptoContent(
        val key: FlipperKeyCrypto
    ) : DeeplinkContent()

    fun length(): Long? {
        return when (this) {
            is ExternalUri -> size
            is InternalStorageFile -> file.length()
            is FFFContent -> flipperFileFormat.length()
            is FFFCryptoContent -> null
        }
    }

    fun filename(): String? {
        return when (this) {
            is ExternalUri -> filename
            is InternalStorageFile -> file.name
            is FFFContent -> filename
            is FFFCryptoContent -> key.pathToKey
        }
    }
}
