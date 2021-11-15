package com.flipperdevices.share.receive.util

import android.content.ContentResolver
import com.flipperdevices.deeplink.model.DeeplinkContent

fun DeeplinkContent.filename(contentResolver: ContentResolver): String? {
    return when (this) {
        is DeeplinkContent.ExternalUri -> {
            uri.filename(contentResolver)
        }
        is DeeplinkContent.InternalStorageFile -> {
            file.name
        }
    }
}
