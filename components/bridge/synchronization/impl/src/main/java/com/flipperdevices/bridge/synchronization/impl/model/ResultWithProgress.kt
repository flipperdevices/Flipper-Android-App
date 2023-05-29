package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.coroutines.flow.Flow

sealed class ResultWithProgress<T> {
    class InProgress<T>(
        val currentPosition: Long = 0L,
        val maxPosition: Long = 0L,
        val text: String? = null
    ) : ResultWithProgress<T>()

    class Completed<T>(val result: T) : ResultWithProgress<T>()
}

suspend fun <T> Flow<ResultWithProgress<T>>.trackProgressAndReturn(
    onProgressUpdate: suspend (ResultWithProgress.InProgress<T>) -> Unit
): T {
    var resultObject: T? = null
    collect { result ->
        when (result) {
            is ResultWithProgress.InProgress -> {
                onProgressUpdate(result)
            }
            is ResultWithProgress.Completed -> {
                resultObject = result.result
            }
        }
    }
    return resultObject ?: error("Result for flow with progress can't be null")
}
