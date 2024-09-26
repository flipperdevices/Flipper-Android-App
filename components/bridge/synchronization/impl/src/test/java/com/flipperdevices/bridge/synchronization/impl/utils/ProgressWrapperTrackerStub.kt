package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.core.progress.DetailedProgressWrapperTracker

internal fun detailedProgressWrapperTrackerStub(): DetailedProgressWrapperTracker {
    return DetailedProgressWrapperTracker(min = 0f, max = 1.0f, progressListener = { _, _ -> })
}
