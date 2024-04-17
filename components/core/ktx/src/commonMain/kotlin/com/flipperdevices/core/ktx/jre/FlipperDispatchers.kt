package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * To be able to mock dispatchers
 */
object FlipperDispatchers {
    fun getDefault() = Dispatchers.Default


    private val WORK_STEALING_POOL by lazy {
        Executors.newWorkStealingPool()
    }
    /**
     * This dispatcher is used to bypass limitations of [Dispatchers.Default] on wearOS
     */
    @FlipperWorkStealingDispatcher
    fun workStealingDispatcher(): CoroutineDispatcher {
        return WORK_STEALING_POOL.asCoroutineDispatcher()
    }
}
