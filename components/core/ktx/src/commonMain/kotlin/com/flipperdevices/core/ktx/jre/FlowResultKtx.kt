package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<Result<T>>.mapCatching(
    crossinline transform: suspend (value: T) -> R
): Flow<Result<R>> = map { result ->
    result.mapCatching {
        transform(it)
    }
}

fun <T> Iterable<Result<Iterable<T>>>.flattenCatching(): Result<List<T>> {
    val result = ArrayList<T>()
    for (element in this) {
        element
            .onSuccess { result.addAll(it) }
            .onFailure { return Result.failure(it) }
    }
    return Result.success(result)
}

/**
 * The correct way to handle an error on flow is to use Flow#onError.
 * But we can't just make a Flow<T> interface - because that's dangerous.
 * We might forget to process the result.
 * So in order for you to handle errors on Flow correctly you need to specifically call this method
 * and be prepared for errors.
 *
 * Example of use:
 *
 * rpcFeatureApi.request(Main().wrapToRequest())
 *  .toThrowableFlow()
 *  .catch { throwable ->
 *   // Catch error
 *  }
 *  .collect {
 *   // Do action
 *  }
 */
fun <T> Flow<Result<T>>.toThrowableFlow(): Flow<T> = map { it.getOrThrow() }
