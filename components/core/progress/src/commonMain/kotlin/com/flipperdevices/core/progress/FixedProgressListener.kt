package com.flipperdevices.core.progress

fun interface FixedProgressListener {
    suspend fun onProgress(current: Long, max: Long)
}
