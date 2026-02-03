package com.flipperdevices.bridge.connection.feature.rpc.storage

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.protobuf.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FRequestStorageTest {
    lateinit var subject: FRequestStorage

    @Before
    fun setUp() {
        subject = FRequestStorage()
    }

    @Test
    fun `Check that object with highest priority returns first`() = runBlocking {
        val lowPriority = Main().wrapToRequest(FlipperRequestPriority.BACKGROUND)
        val mediumPriority = Main().wrapToRequest()
        val highestPriority = Main().wrapToRequest(FlipperRequestPriority.FOREGROUND)

        subject.sendRequest(mediumPriority, lowPriority, highestPriority)

        assertEquals(highestPriority, subject.getNextRequest())
        assertEquals(mediumPriority, subject.getNextRequest())
        assertEquals(lowPriority, subject.getNextRequest())
    }

    @Test
    fun `Check that oldest object returns first`() = runBlocking {
        val lowPriority = FlipperRequest(Main(), 300L)
        val mediumPriority = FlipperRequest(Main(), 200L)
        val highestPriority = FlipperRequest(Main(), 100L)

        subject.sendRequest(mediumPriority, lowPriority, highestPriority)

        assertEquals(highestPriority, subject.getNextRequest())
        assertEquals(mediumPriority, subject.getNextRequest())
        assertEquals(lowPriority, subject.getNextRequest())
    }

    @Test
    fun `Suspends when queue is empty`() = runBlocking {
        val request = Main().wrapToRequest(FlipperRequestPriority.BACKGROUND)

        subject.sendRequest(request)

        assertEquals(request, subject.getNextRequest())

        // getNextRequest should suspend when queue is empty
        // Use timeout to verify it doesn't return immediately
        val result = withTimeoutOrNull(100) {
            subject.getNextRequest()
        }
        assertNull(result)
    }

    @Test
    fun `Resumes when request is added`() = runBlocking {
        val request = Main().wrapToRequest(FlipperRequestPriority.BACKGROUND)

        // Start waiting for request in background
        val deferred = async {
            subject.getNextRequest()
        }

        // Give it time to start suspending
        delay(50)

        // Add request - should resume the suspended coroutine
        subject.sendRequest(request)

        val result = withTimeoutOrNull(100) {
            deferred.await()
        }
        assertEquals(request, result)
    }
}
