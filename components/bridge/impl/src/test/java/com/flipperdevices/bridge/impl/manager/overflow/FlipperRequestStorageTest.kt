package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FlipperRequestStorageTest {
    lateinit var subject: FlipperRequestStorage

    @Before
    fun setUp() {
        subject = FlipperRequestStorageImpl()
    }

    @Test
    fun `Check that object with highest priority returns first`() = runBlocking {
        val lowPriority = main { }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        val mediumPriority = main { }.wrapToRequest()
        val highestPriority = main { }.wrapToRequest(FlipperRequestPriority.FOREGROUND)

        subject.sendRequest(mediumPriority, lowPriority, highestPriority)

        assertEquals(highestPriority, subject.getNextRequest(timeout = 100))
        assertEquals(mediumPriority, subject.getNextRequest(timeout = 100))
        assertEquals(lowPriority, subject.getNextRequest(timeout = 100))
    }

    @Test
    fun `Return null if not present request`() = runBlocking {
        val request = main { }.wrapToRequest(FlipperRequestPriority.BACKGROUND)

        subject.sendRequest(request)

        assertEquals(request, subject.getNextRequest(timeout = 100))
        assertNull(subject.getNextRequest(timeout = 100))
    }
}
