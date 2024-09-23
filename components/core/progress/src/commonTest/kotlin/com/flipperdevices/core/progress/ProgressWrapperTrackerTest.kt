package com.flipperdevices.core.progress

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProgressWrapperTrackerTest {

    private lateinit var subject: ProgressWrapperTracker

    private val progressListener: ProgressListener = mockk()

    @Before
    fun setUp() {
        coEvery { progressListener.onProgress(any()) } returns Unit
        subject = ProgressWrapperTracker(progressListener, 1f, 100F)
    }

    @Test
    fun onProgress_whenDiffIsLessThanZero_thenNotCallProgressListener() = runTest {
        subject = ProgressWrapperTracker(progressListener, 100f, 1f)
        subject.onProgress(1f)

        coVerify(exactly = 0) { progressListener.onProgress(any()) }
    }

    @Test
    fun onProgress_whenDiffIsBiggerZero_thenCallProgressListener() = runTest {
        subject = ProgressWrapperTracker(progressListener, 0f, 10f)
        subject.onProgress(1f)

        coVerify(exactly = 1) { progressListener.onProgress(any()) }
    }

    @Test
    fun onProgress_whenCurrentIsBiggerThanMax_thenReturnDefaultMaxWithDebugException() = runTest {
        try {
            subject.onProgress(10, 1)
            coVerify(exactly = 1) { progressListener.onProgress(100f) }
        } catch (e: Exception) {
            Assert.assertTrue(e is IllegalStateException)
        }
    }

    @Test
    fun onProgress_whenMaxIsEqualToZero_thenReturnDefaultMaxWithDebugException() = runTest {
        try {
            subject.onProgress(10, 0)
            coVerify(exactly = 1) { progressListener.onProgress(100f) }
        } catch (e: Exception) {
            Assert.assertTrue(e is IllegalStateException)
        }
    }

    @Test
    fun onProgress_whenWithCorrectValues_thenCalculateValue() = runTest {
        subject.onProgress(10, 20)

        coVerify(exactly = 1) { progressListener.onProgress(1f) }
    }
}
