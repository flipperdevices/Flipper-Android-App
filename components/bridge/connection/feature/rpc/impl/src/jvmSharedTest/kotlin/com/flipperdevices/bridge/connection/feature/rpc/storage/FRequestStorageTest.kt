package com.flipperdevices.bridge.connection.feature.rpc.storage

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.protobuf.Main
import kotlinx.coroutines.runBlocking
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

        assertEquals(highestPriority, subject.getNextRequest(timeout = 100))
        assertEquals(mediumPriority, subject.getNextRequest(timeout = 100))
        assertEquals(lowPriority, subject.getNextRequest(timeout = 100))
    }

    @Test
    fun `Check that oldest object returns first`() = runBlocking {
        val lowPriority = FlipperRequest(Main(), 300L)
        val mediumPriority = FlipperRequest(Main(), 200L)
        val highestPriority = FlipperRequest(Main(), 100L)

        subject.sendRequest(mediumPriority, lowPriority, highestPriority)

        assertEquals(highestPriority, subject.getNextRequest(timeout = 100))
        assertEquals(mediumPriority, subject.getNextRequest(timeout = 100))
        assertEquals(lowPriority, subject.getNextRequest(timeout = 100))
    }

    @Test
    fun `Return null if not present request`() = runBlocking {
        val request = Main().wrapToRequest(FlipperRequestPriority.BACKGROUND)

        subject.sendRequest(request)

        assertEquals(request, subject.getNextRequest(timeout = 100))
        assertNull(subject.getNextRequest(timeout = 100))
    }
}
