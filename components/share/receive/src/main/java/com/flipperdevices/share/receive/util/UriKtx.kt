package com.flipperdevices.share.receive.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.FileNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val FAILED_SIZE = -1L
private const val FILE_DESCRIPTOR_FAILED_SIZE = -1L

/**
 * @return FAILED_SIZE if we can't provide size
 */
@Suppress("SwallowedException")
fun Uri.length(contentResolver: ContentResolver): Long {
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
        return FAILED_SIZE
    }
    return contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)
        ?.use { cursor ->
            // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex == -1) {
                return@use FAILED_SIZE
            }
            cursor.moveToFirst()
            return try {
                cursor.getLong(sizeIndex)
            } catch (_: Throwable) {
                FAILED_SIZE
            }
        } ?: FAILED_SIZE
}

suspend fun Uri.lengthAsync(contentResolver: ContentResolver): Long = withContext(Dispatchers.IO) {
    return@withContext length(contentResolver)
}
