package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * To be able to mock dispatchers
 */
object FlipperDispatchers {
    fun getDefault() = Dispatchers.IO

    private val AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors()

    /**
     * This dispatcher is used to bypass limitations of [Dispatchers.Default] on wearOS
     */
    @FlipperThreadPoolDispatcher
    fun fixedThreadPool(nThreads: Int = AVAILABLE_PROCESSORS): CoroutineDispatcher {
        return Executors.newFixedThreadPool(nThreads).asCoroutineDispatcher()
    }
}
