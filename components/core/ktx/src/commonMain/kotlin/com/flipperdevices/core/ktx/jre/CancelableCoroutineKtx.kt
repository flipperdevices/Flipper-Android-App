package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

suspend inline fun <T> withCoroutineScope(
    crossinline block: suspend (CoroutineScope) -> T
): T {
    val scope = CoroutineScope(FlipperDispatchers.workStealingDispatcher)
    return try {
        block(scope)
    } finally {
        scope.cancel()
    }
}
