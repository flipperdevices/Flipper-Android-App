package com.flipperdevices.bridge.dao.impl.thread

import kotlin.jvm.Throws

fun interface MainThreadChecker {
    /**
     * Checks for main thread and throws [IllegalStateException] with [message] if it's not
     */
    @Throws(IllegalStateException::class)
    fun checkMainThread(message: () -> String)
}
