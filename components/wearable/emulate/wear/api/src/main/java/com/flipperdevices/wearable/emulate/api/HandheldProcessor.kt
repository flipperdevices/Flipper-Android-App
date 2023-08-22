package com.flipperdevices.wearable.emulate.api

import kotlinx.coroutines.CoroutineScope

interface HandheldProcessor {
    fun init(scope: CoroutineScope)

    suspend fun reset()
}
