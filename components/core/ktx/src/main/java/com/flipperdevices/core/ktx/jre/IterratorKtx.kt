package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Parallel map
 */
suspend fun <A, B> Iterable<A>.pmap(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend (A) -> B
): List<B> = coroutineScope {
    map { async(context) { block(it) } }.awaitAll()
}

inline fun <T> Iterable<T>.forEachIterable(block: (T) -> Unit) {
    with(iterator()) {
        while (hasNext()) {
            block(next())
        }
    }
}
