package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class WaitNotifyLock {
    private var waitFlow = MutableSharedFlow<Unit>()

    suspend fun wait() = waitFlow.first()

    suspend fun notifyAll() {
        waitFlow.emit(Unit)
    }
}
