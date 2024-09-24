package com.flipperdevices.core.progress

fun interface ProgressListener {
    suspend fun onProgress(current: Float)
}
