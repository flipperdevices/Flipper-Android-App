package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EmulateHelperTest {
    private lateinit var flipperKey: FlipperKey
    private lateinit var fileType: FlipperFileType
    private lateinit var requestApi: FlipperRequestApi
    private lateinit var underTest: EmulateHelper

    @Before
    fun setUp() {
        underTest = EmulateHelper()
        requestApi = mockk()
        fileType = mockk()
        flipperKey = mockk()
    }

    @Test
    fun `respect emulate order`() = runTest {
    }
}
