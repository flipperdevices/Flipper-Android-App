package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Parallel map
 */
suspend fun <A, B> Iterable<A>.pmap(block: suspend (A) -> B): List<B> = coroutineScope {
    map { async { block(it) } }.awaitAll()
}

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Eagerly,
    mapper(value)
)

inline fun <T> Iterable<T>.forEachIterable(block: (T) -> Unit) {
    with(iterator()) {
        while (hasNext()) {
            block(next())
        }
    }
}
