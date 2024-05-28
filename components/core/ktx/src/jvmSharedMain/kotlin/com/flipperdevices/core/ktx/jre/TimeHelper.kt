package com.flipperdevices.core.ktx.jre

/**
 * To be able to mock time
 */
object TimeHelper {
    fun getNow() = System.currentTimeMillis()
    fun getNanoTime() = System.nanoTime()
}
