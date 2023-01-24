package com.flipperdevices.bridge.synchronization.impl.utils

fun progressWrapperTrackerStub(): ProgressWrapperTracker {
    return ProgressWrapperTracker(min = 0f, max = 1.0f, progressListener = {})
}