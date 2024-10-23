package com.flipperdevices.core.progress

fun interface DetailedProgressListener {
    interface Detail

    suspend fun onProgress(current: Float, detail: Detail)
}
