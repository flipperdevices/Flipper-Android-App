package com.flipperdevices.bridge.dao.impl.thread

internal object StubMainThreadChecker : MainThreadChecker {
    override fun checkMainThread(message: () -> String) = Unit
}
