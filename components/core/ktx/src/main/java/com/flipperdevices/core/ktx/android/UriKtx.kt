package com.flipperdevices.core.ktx.android

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFileName(contentResolver: ContentResolver): String? {
    var nameFromResolver: String? = null
    if (scheme == ContentResolver.SCHEME_CONTENT) {
        contentResolver.query(this, null, null, null, null).use {
            val cursor = it ?: return@use
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex == -1) {
                return@use
            }
            nameFromResolver = cursor.getString(columnIndex)
        }
    }

    if (nameFromResolver != null) {
        return nameFromResolver
    }

    return path?.substringAfterLast("/")
}
