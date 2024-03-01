package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.Dispatchers

/**
 * To be able to mock dispatchers
 */
object FlipperDispatchers {
    fun getDefault() = Dispatchers.Default
}
