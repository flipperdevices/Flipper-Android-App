package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.core.progress.ProgressWrapperTracker

internal fun progressWrapperTrackerStub(): ProgressWrapperTracker {
    return ProgressWrapperTracker(min = 0f, max = 1.0f, progressListener = { _, _ -> })
}
