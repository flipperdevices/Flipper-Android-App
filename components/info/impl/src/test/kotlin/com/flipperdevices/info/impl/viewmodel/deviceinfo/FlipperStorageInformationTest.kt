package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.metric.api.MetricApi
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class FlipperStorageInformationTest {
    private lateinit var metricApi: MetricApi
    private lateinit var underTest: FlipperStorageInformationApi

    @Before
    fun setUp() {
        metricApi = mockk()
        underTest = FlipperStorageInformationApiImpl(metricApi)
    }

    @Test
    fun `request storage info first time`() {

    }
}