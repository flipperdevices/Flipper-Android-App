package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

sealed class ResultWithProgress<T> {
    class InProgress<T>(
        val currentPosition: Int = 0,
        val maxPosition: Int = 0,
        val text: String? = null
    ) : ResultWithProgress<T>()

    class Completed<T>(val result: T) : ResultWithProgress<T>()
}

suspend fun <T> Flow<ResultWithProgress<T>>.trackProgressAndReturn(
    onProgressUpdate: (ResultWithProgress.InProgress<T>) -> Unit
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
