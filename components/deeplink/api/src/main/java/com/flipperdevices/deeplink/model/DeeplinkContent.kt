package com.flipperdevices.deeplink.model

import android.net.Uri
import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.Parcelize

sealed class DeeplinkContent : Parcelable {
    @Parcelize
    class ExternalUri(
        val filename: String?,
        val size: Long?,
        val uri: Uri
    ) : DeeplinkContent()

    @Parcelize
    class InternalStorageFile(
        val file: File
    ) : DeeplinkContent()

    fun length(): Long? {
        return when (this) {
            is ExternalUri -> size
            is InternalStorageFile -> file.length()
        }
    }

    fun filename(): String? {
        return when (this) {
            is ExternalUri -> filename
            is InternalStorageFile -> file.name
        }
    }
}
