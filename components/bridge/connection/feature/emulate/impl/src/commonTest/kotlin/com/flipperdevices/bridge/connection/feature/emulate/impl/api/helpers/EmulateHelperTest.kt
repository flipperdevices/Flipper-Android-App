package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.AppEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StartEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StopEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.test.LoggerRuleFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.ClassRule
import org.junit.Test

private val KEY_TYPE = FlipperKeyType.SUB_GHZ
private val TEST_KEY_PATH = FlipperFilePath(KEY_TYPE.flipperDir, "test.${KEY_TYPE.extension}")

class EmulateHelperTest {
    private val callOrder = mutableListOf<String>()
    private val startEmulateHelper: StartEmulateHelper = mockk {
        coEvery { onStart(any(), any(), any(), any()) } coAnswers { true }
    }
    private val stopEmulateHelper: StopEmulateHelper = mockk {
        coEvery { onStop(any()) } coAnswers {
            callOrder.add("ButtonRelease")
        }
    }
    private val appEmulateHelper: AppEmulateHelper = mockk {
        coEvery { tryOpenApp(any(), any()) } coAnswers {
            callOrder.add("AppLoad")
            true
        }
    }
    private val emulateHelper: EmulateHelper = EmulateHelperImpl(
        startEmulateHelper = startEmulateHelper,
        stopEmulateHelper = stopEmulateHelper
    )

    @Test
    fun `respect emulate order`() = runTest {
        val emulateConfig = EmulateConfig(KEY_TYPE, TEST_KEY_PATH)

        appEmulateHelper.tryOpenApp(this, KEY_TYPE)
        emulateHelper.startEmulate(
            this,
            emulateConfig
        )
        emulateHelper.stopEmulate(this@runTest)

        appEmulateHelper.tryOpenApp(this, KEY_TYPE)
        emulateHelper.startEmulate(
            this,
            emulateConfig
        )
        emulateHelper.stopEmulate(this@runTest)

        appEmulateHelper.tryOpenApp(this, KEY_TYPE)
        emulateHelper.startEmulate(
            this,
            emulateConfig
        )

        val expectedAction = arrayOf(
            "AppLoad",
            "ButtonRelease",
            "AppLoad",
            "ButtonRelease",
            "AppLoad"
        )
        Assert.assertArrayEquals(expectedAction, callOrder.toTypedArray())
    }

    companion object {
        @get:ClassRule
        @JvmStatic
        var timberRule = LoggerRuleFactory.create()
    }
}
