package com.flipperdevices.core.progress

fun interface ProgressListener {
    interface Detail

    suspend fun onProgress(current: Float)
}

fun interface DetailedProgressListener {
    interface Detail

    suspend fun onProgress(current: Float, detail: Detail)
}