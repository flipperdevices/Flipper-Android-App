package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FLagsDetectorFeatureTest {
    private lateinit var underTest: FLagsDetectorFeature
    private lateinit var restartRpcFeatureApi: FRestartRpcFeatureApi

    @Before
    fun setUp() {
        restartRpcFeatureApi = mockk()
    }

    @Test
    fun `reset rpc on pending overflow`() = runTest {
        /*val scope = this
        underTest = FLagsDetectorFeatureImpl(
            scope = scope,
            restartRpcFeatureApi = restartRpcFeatureApi
        )
        underTest.notifyAboutAction()*/
    }
}