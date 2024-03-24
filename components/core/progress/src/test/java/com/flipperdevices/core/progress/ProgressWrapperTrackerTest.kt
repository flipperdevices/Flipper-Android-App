package com.flipperdevices.core.progress

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class ProgressWrapperTrackerTest {

    private lateinit var subject: ProgressWrapperTracker

    private val progressListener: ProgressListener = mock()

    @Before
    fun setUp() {
        subject = ProgressWrapperTracker(progressListener, 1f, 100F)
    }

    @Test
    fun onProgress_whenDiffIsLessThanZero_thenNotCallProgressListener() = runTest {
        subject = ProgressWrapperTracker(progressListener, 100f, 1f)
        subject.onProgress(1f)

        verify(progressListener, never()).onProgress(any())
    }

    @Test
    fun onProgress_whenDiffIsBiggerZero_thenCallProgressListener() = runTest {
        subject = ProgressWrapperTracker(progressListener, 0f, 10f)
        subject.onProgress(1f)

        verify(progressListener).onProgress(any())
    }

    @Test
    fun report_whenCurrentIsBiggerThanMax_thenReturnDefaultMaxWithDebugException() = runTest {
        try {
            subject.report(10, 1)
            verify(progressListener).onProgress(100f)
        } catch (e: Exception) {
            Assert.assertTrue(e is IllegalStateException)
        }
    }

    @Test
    fun report_whenMaxIsEqualToZero_thenReturnDefaultMaxWithDebugException() = runTest {
        try {
            subject.report(10, 0)
            verify(progressListener).onProgress(100f)
        } catch (e: Exception) {
            Assert.assertTrue(e is IllegalStateException)
        }
    }

    @Test
    fun report_whenWithCorrectValues_thenCalculateValue() = runTest {
        subject.report(10, 20)

        verify(progressListener).onProgress(1F)
    }
}
