package com.flipperdevices.deeplink.model

import android.net.Uri
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import java.io.File
import kotlinx.parcelize.Parcelize

sealed class DeeplinkContent : Parcelable {
    @Parcelize
    data class FFFContent(
        val filename: String,
        val content: FlipperFileFormat
    ) : DeeplinkContent()

    @Parcelize
    data class ExternalUri(
        val filename: String?,
        val size: Long?,
        val uri: Uri
    ) : DeeplinkContent()

    @Parcelize
    data class InternalStorageFile(
        val file: File
    ) : DeeplinkContent()

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
}
