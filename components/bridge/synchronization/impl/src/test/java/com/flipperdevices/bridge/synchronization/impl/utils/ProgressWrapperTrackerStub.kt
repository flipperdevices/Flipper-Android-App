package com.flipperdevices.bridge.synchronization.impl.utils

internal fun progressWrapperTrackerStub(): ProgressWrapperTracker {
    return ProgressWrapperTracker(min = 0f, max = 1.0f, progressListener = {})
}
