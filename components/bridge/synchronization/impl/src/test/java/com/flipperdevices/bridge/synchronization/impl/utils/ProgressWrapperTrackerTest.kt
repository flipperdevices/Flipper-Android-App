package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.core.progress.ProgressWrapperTracker
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ProgressWrapperTrackerTest {
    @Test
    fun `simple progress`() = runTest {
        var percent = 0f
        val tracker = ProgressWrapperTracker(min = 0f, max = 1f, progressListener = {
            percent = it
        })

        tracker.onProgress(5, 10)

        Assert.assertEquals(0.5f, percent, 0.0001f)
    }

    @Test
    fun `wrap progress`() = runTest {
        var percent = 0f
        val originalTracker = ProgressWrapperTracker(min = 0f, max = 1f, progressListener = {
            percent = it
        })
        val tracker = ProgressWrapperTracker(
            min = 0.25f,
            max = 0.75f,
            progressListener = originalTracker
        )

        tracker.onProgress(2, 10)

        Assert.assertEquals(0.35f, percent, 0.0001f)
    }
}
