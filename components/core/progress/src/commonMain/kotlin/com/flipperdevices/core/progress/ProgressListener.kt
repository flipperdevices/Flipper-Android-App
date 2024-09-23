package com.flipperdevices.core.progress

fun interface ProgressListener {
    interface Detail

    suspend fun onProgress(current: Float, detail: Detail?)

    companion object {
        /**
         * This extension is required because we can't
         * have default value with functional interface
         */
        suspend fun ProgressListener.onProgress(current: Float) = onProgress(current, null)
    }
}
