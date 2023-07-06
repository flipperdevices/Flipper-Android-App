package com.flipperdevices.bridge.api.manager

import kotlinx.coroutines.CoroutineScope

interface FlipperReadyListener {
    suspend fun onFlipperReady(scope: CoroutineScope)
}
