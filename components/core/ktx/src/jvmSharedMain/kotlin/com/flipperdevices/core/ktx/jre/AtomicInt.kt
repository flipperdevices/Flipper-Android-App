package com.flipperdevices.core.ktx.jre

import java.util.concurrent.atomic.AtomicInteger

/**
 * Copy from AtomicInteger#updateAndGet
 */
fun AtomicInteger.updateAndGetSafe(updater: (Int) -> Int): Int {
    var prev: Int
    var next: Int
    do {
        prev = get()
        next = updater(prev)
    } while (!compareAndSet(prev, next))
    return next
}
