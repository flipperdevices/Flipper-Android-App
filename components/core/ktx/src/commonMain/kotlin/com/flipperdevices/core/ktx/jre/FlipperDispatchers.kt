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

    fun createNewWorkStealingDispatcher(): CoroutineDispatcher {
        return Executors.newWorkStealingPool().asCoroutineDispatcher()
    }

    val workStealingDispatcher: CoroutineDispatcher by lazy {
        createNewWorkStealingDispatcher()
    }
}
