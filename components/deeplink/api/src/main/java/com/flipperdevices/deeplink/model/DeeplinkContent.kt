package com.flipperdevices.deeplink.model

import android.net.Uri
import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.Parcelize

sealed class DeeplinkContent : Parcelable {
    @Parcelize
    class ExternalUri(
        val uri: Uri
    ) : DeeplinkContent()

    @Parcelize
    class InternalStorageFile(
        val file: File
    ) : DeeplinkContent()
}
