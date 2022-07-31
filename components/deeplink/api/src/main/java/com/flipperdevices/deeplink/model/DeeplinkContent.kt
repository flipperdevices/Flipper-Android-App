package com.flipperdevices.deeplink.model

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import java.io.File
import java.io.InputStream
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class DeeplinkContent : Parcelable {
    @Parcelize
    @Serializable
    data class FFFContent(
        val filename: String,
        val content: FlipperFileFormat
    ) : DeeplinkContent()

    @Parcelize
    @Serializable
    data class ExternalUri(
        val filename: String?,
        val size: Long?,
        val uriString: String
    ) : DeeplinkContent() {
        @IgnoredOnParcel
        val uri by lazy { Uri.parse(uriString) }
    }

    @Parcelize
    @Serializable
    data class InternalStorageFile(
        val filePath: String
    ) : DeeplinkContent() {
        @IgnoredOnParcel
        val file by lazy { File(filePath) }
    }

    fun length(): Long? {
        return when (this) {
            is ExternalUri -> size
            is InternalStorageFile -> file.length()
            is FFFContent -> content.length()
        }
    }

    fun filename(): String? {
        return when (this) {
            is ExternalUri -> filename
            is InternalStorageFile -> file.name
            is FFFContent -> filename
        }
    }

    fun openStream(contentResolver: ContentResolver): InputStream? {
        return when (this) {
            is ExternalUri -> {
                contentResolver.openInputStream(uri)
            }
            is InternalStorageFile -> {
                file.inputStream()
            }
            is FFFContent -> content.openStream()
        }
    }

    fun cleanUp(contentResolver: ContentResolver) {
        when (this) {
            is ExternalUri -> {
                contentResolver.releasePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            is InternalStorageFile -> {
                file.delete()
            }
            is FFFContent -> {} // Noting
        }
    }
}
