package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FLagsDetectorFeatureTest {
    private lateinit var underTest: FLagsDetectorFeature
    private lateinit var restartRpcFeatureApi: FRestartRpcFeatureApi

    @Before
    fun setUp() {
        restartRpcFeatureApi = mockk(relaxed = true)
    }

    @Test
    fun `reset rpc on pending overflow`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val childScope = TestScope(testScheduler)
        mockkObject(FlipperDispatchers)
        every {
            FlipperDispatchers.workStealingDispatcher
        } returns testDispatcher

        underTest = FLagsDetectorFeatureImpl(
            scope = childScope,
            restartRpcFeatureApi = restartRpcFeatureApi,
            flipperActionNotifier = FlipperActionNotifierImpl(childScope)
        )
        val backgroundJob = backgroundScope.launch {
            underTest.wrapPendingAction(mockk(relaxed = true)) {
                delay(Long.MAX_VALUE)
                println("Hello from pending action!")
            }
        }
        underTest.notifyAboutAction()
        childScope.advanceUntilIdle()

        coVerify { restartRpcFeatureApi.restartRpc() }
        backgroundJob.cancelAndJoin()
        childScope.cancel()
    }

    @Test
    fun `not reset rpc without pending overflow`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val childScope = TestScope(testScheduler)
        mockkObject(FlipperDispatchers)
        every {
            FlipperDispatchers.workStealingDispatcher
        } returns testDispatcher

        underTest = FLagsDetectorFeatureImpl(
            scope = childScope,
            restartRpcFeatureApi = restartRpcFeatureApi,
            flipperActionNotifier = FlipperActionNotifierImpl(childScope)
        )
        val backgroundJob = backgroundScope.launch {
            underTest.wrapPendingAction(mockk()) {
                println("Hello world!")
            }
        }
        backgroundJob.join()
        underTest.notifyAboutAction()
        childScope.advanceUntilIdle()

        coVerify(inverse = true) { restartRpcFeatureApi.restartRpc() }
        backgroundJob.cancelAndJoin()
        childScope.cancel()
    }
}
