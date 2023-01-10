package com.flipperdevices.core.ktx.jre

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.FileNotFoundException

private const val FILE_DESCRIPTOR_FAILED_SIZE = -1L
private const val COLUMN_FAILED_SIZE = -1

/**
 * @return FAILED_SIZE if we can't provide size
 */
@Suppress("SwallowedException")
fun Uri.length(contentResolver: ContentResolver): Long? {
    val assetFileDescriptor = try {
        contentResolver.openAssetFileDescriptor(this, "r")
    } catch (e: FileNotFoundException) {
        null
    }
    // uses ParcelFileDescriptor#getStatSize underneath if failed
    val length = assetFileDescriptor?.use { it.length } ?: FILE_DESCRIPTOR_FAILED_SIZE
    if (length != FILE_DESCRIPTOR_FAILED_SIZE) {
        return length
    }

    // if "content://" uri scheme, try contentResolver table
    if (!scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        return null
    }
    return contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)
        ?.use { cursor ->
            // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex == COLUMN_FAILED_SIZE) {
                return@use null
            }
            cursor.moveToFirst()
            return try {
                cursor.getLong(sizeIndex)
            } catch (_: Throwable) {
                null
            }
        }
}

fun Uri.filename(contentResolver: ContentResolver): String? {
    val nameFromResolver: String? = if (scheme == ContentResolver.SCHEME_CONTENT) {
        runCatching {
            contentResolver.query(this, null, null, null, null).use {
                val cursor = it ?: return@use null
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex == -1) {
                    return@use null
                }
                return@use cursor.getString(columnIndex)
            }
        }.getOrNull()
    } else {
        null
    }

    if (nameFromResolver != null) {
        return nameFromResolver
    }

    return path?.substringAfterLast("/")
}
