package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import kotlin.time.Duration

@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceAfterFirst(timeout: Duration): Flow<T> {
    return withIndex().debounce {
        if (it.index == 0) {
            0L
        } else {
            timeout.inWholeMilliseconds
        }
    }.map { it.value }
}
