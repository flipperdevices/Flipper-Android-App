package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
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

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope + Dispatchers.Default,
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
