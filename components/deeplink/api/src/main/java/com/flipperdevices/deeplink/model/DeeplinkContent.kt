package com.flipperdevices.deeplink.model

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperKeyCrypto
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.io.File
import java.io.InputStream

@Serializable
sealed class DeeplinkContent : Parcelable {
    @Parcelize
    @Serializable
    data class FFFContent(
        val filename: String,
        val flipperFileFormat: FlipperFileFormat
    ) : DeeplinkContent()

    @Parcelize
    @Serializable
    data class ExternalUri(
        val filename: String?,
        val size: Long?,
        val uriString: String
    ) : DeeplinkContent() {
        @IgnoredOnParcel
        val uri: Uri by lazy { Uri.parse(uriString) }
    }

    @Parcelize
    @Serializable
    data class InternalStorageFile(
        val filePath: String
    ) : DeeplinkContent() {
        @IgnoredOnParcel
        val file by lazy { File(filePath) }
    }

    @Parcelize
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

    fun openStream(contentResolver: ContentResolver): InputStream? {
        return when (this) {
            is ExternalUri -> contentResolver.openInputStream(uri)
            is InternalStorageFile -> file.inputStream()
            is FFFContent -> flipperFileFormat.openStream()
            is FFFCryptoContent -> null
        }
    }

    fun cleanUp(contentResolver: ContentResolver) {
        when (this) {
            is ExternalUri ->
                contentResolver.releasePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            is InternalStorageFile -> file.delete()
            is FFFContent -> {} // Nothing
            is FFFCryptoContent -> {} // Nothing
        }
    }
}
