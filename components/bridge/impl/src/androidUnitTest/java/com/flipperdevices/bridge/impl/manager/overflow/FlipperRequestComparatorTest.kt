package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.protobuf.main
import org.junit.Assert
import org.junit.Test

class FlipperRequestComparatorTest {
    private val subject = FlipperRequestComparator()

    @Test
    fun `Highest priority larger`() {
        val backgroundRequest =
            FlipperRequest(main { }, priority = FlipperRequestPriority.BACKGROUND)
        val foregroundRequest =
            FlipperRequest(main { }, priority = FlipperRequestPriority.FOREGROUND)

        val result = subject.compare(backgroundRequest, foregroundRequest)

        Assert.assertEquals(1, result)
    }

    @Test
    fun `Oldest priority larger`() {
        val firstRequest = FlipperRequest(main { }, 100L)
        val secondRequest = FlipperRequest(main { }, 200L)

        val result = subject.compare(secondRequest, firstRequest)

        Assert.assertEquals(1, result)
    }
}
