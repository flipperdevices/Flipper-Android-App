package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Parallel map
 */
suspend fun <A, B> Iterable<A>.pmap(block: suspend (A) -> B): List<B> = coroutineScope {
    map { async { block(it) } }.awaitAll()
}
