package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

fun MutableList<Job>.cancelAndClear() {
    forEachIterable {
        it.cancel()
    }
    clear()
}

suspend fun MutableList<Job>.cancelJoinAndClear() {
    forEachIterable {
        it.cancelAndJoin()
    }
    clear()
}
