package com.flipperdevices.firstpair.impl.viewmodels.connecting

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.test.TimberRule
import com.flipperdevices.core.test.mockScope
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModelCoroutineScopeProvider
import com.flipperdevices.firstpair.impl.model.DevicePairState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class PairDeviceViewModelTest {
    @get:Rule
    var timberRule = TimberRule()

    private lateinit var underTest: PairDeviceViewModel
    private lateinit var bleManager: FirstPairBleManager
    private lateinit var stateFlow: MutableStateFlow<ConnectionState>
    private lateinit var firstPairBleManagerFactory: FirstPairBleManager.Factory

    @Before
    fun setUp() {
        stateFlow = MutableStateFlow(
            ConnectionState.Disconnected(ConnectionState.Disconnected.Reason.UNKNOWN)
        )
        bleManager = mockk(relaxUnitFun = true)

        mockkStatic("com.flipperdevices.bridge.api.manager.ktx.BleManagerExtKt")

        every {
            bleManager.stateAsFlow()
        } returns stateFlow
        firstPairBleManagerFactory = object : FirstPairBleManager.Factory {
            override fun invoke(scope: CoroutineScope) = bleManager
        }
    }

    @Test
    fun `pair successful`() = runTest {
        underTest = PairDeviceViewModel(
            firstPairBleManagerFactory = firstPairBleManagerFactory,
            deviceColorSaver = mockk(relaxUnitFun = true),
            firstPairStorage = mockk(relaxUnitFun = true),
            dispatcher = StandardTestDispatcher(testScheduler)
        )
        DecomposeViewModelCoroutineScopeProvider.mockScope(this)
        val pairStates = mutableListOf<DevicePairState>()
        val pairStatusJob = underTest.getConnectionState().onEach {
            pairStates.add(it)
            println(it)
        }.launchIn(this)
        val testDevice = makeTestDevice()
        coEvery {
            bleManager.connectToDevice(any())
        } coAnswers { stateFlow.emit(ConnectionState.Ready(FlipperSupportedState.READY)) }
        every {
            bleManager.bluetoothDevice
        } returns testDevice.device

        underTest.startConnectToDevice(testDevice, resetPair = false)
        advanceUntilIdle()
        Assert.assertEquals(
            listOf(
                DevicePairState.NotInitialized,
                DevicePairState.WaitingForStart(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connected(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                )
            ),
            pairStates
        )
        pairStatusJob.cancelAndJoin()
    }

    @Test
    fun `pair successful with connecting state on connect method`() = runTest {
        underTest = PairDeviceViewModel(
            firstPairBleManagerFactory = firstPairBleManagerFactory,
            deviceColorSaver = mockk(relaxUnitFun = true),
            firstPairStorage = mockk(relaxUnitFun = true),
            dispatcher = StandardTestDispatcher(testScheduler)
        )
        DecomposeViewModelCoroutineScopeProvider.mockScope(this)
        val pairStates = mutableListOf<DevicePairState>()
        val pairStatusJob = underTest.getConnectionState().onEach {
            pairStates.add(it)
            println(it)
        }.launchIn(this)
        val testDevice = makeTestDevice()
        coEvery {
            bleManager.connectToDevice(any())
        } coAnswers { stateFlow.emit(ConnectionState.Initializing) }
        every {
            bleManager.bluetoothDevice
        } returns testDevice.device

        underTest.startConnectToDevice(testDevice, resetPair = false)
        advanceUntilIdle()
        stateFlow.emit(ConnectionState.Ready(FlipperSupportedState.READY))
        advanceUntilIdle()
        Assert.assertEquals(
            listOf(
                DevicePairState.NotInitialized,
                DevicePairState.WaitingForStart(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connecting(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connected(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                )
            ),
            pairStates
        )
        pairStatusJob.cancelAndJoin()
    }

    @Test
    fun `pair successful with connecting state on disconnected`() = runTest {
        underTest = PairDeviceViewModel(
            firstPairBleManagerFactory = firstPairBleManagerFactory,
            deviceColorSaver = mockk(relaxUnitFun = true),
            firstPairStorage = mockk(relaxUnitFun = true),
            dispatcher = StandardTestDispatcher(testScheduler)
        )
        DecomposeViewModelCoroutineScopeProvider.mockScope(this)
        val pairStates = mutableListOf<DevicePairState>()
        val pairStatusJob = underTest.getConnectionState().onEach {
            pairStates.add(it)
            println(it)
        }.launchIn(this)
        val testDevice = makeTestDevice()
        coEvery {
            bleManager.connectToDevice(any())
        } coAnswers { stateFlow.emit(ConnectionState.Disconnected(ConnectionState.Disconnected.Reason.UNKNOWN)) }
        every {
            bleManager.bluetoothDevice
        } returns testDevice.device

        advanceUntilIdle()
        underTest.startConnectToDevice(testDevice, resetPair = false)
        advanceUntilIdle()
        stateFlow.emit(ConnectionState.Initializing)
        advanceUntilIdle()
        stateFlow.emit(ConnectionState.Ready(FlipperSupportedState.READY))
        advanceUntilIdle()
        Assert.assertEquals(
            listOf(
                DevicePairState.NotInitialized,
                DevicePairState.WaitingForStart(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connecting(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connected(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                )
            ),
            pairStates
        )
        pairStatusJob.cancelAndJoin()
    }

    @Test
    fun `pair failed on timeout`() = runTest {
        underTest = PairDeviceViewModel(
            firstPairBleManagerFactory = firstPairBleManagerFactory,
            deviceColorSaver = mockk(relaxUnitFun = true),
            firstPairStorage = mockk(relaxUnitFun = true),
            dispatcher = StandardTestDispatcher(testScheduler)
        )
        DecomposeViewModelCoroutineScopeProvider.mockScope(this)
        val pairStates = mutableListOf<DevicePairState>()
        val pairStatusJob = underTest.getConnectionState().onEach {
            pairStates.add(it)
            println(it)
        }.launchIn(this)
        val testDevice = makeTestDevice()
        coEvery {
            bleManager.connectToDevice(any())
        } coAnswers { withTimeout(-1) {} }
        every {
            bleManager.bluetoothDevice
        } returns testDevice.device

        underTest.startConnectToDevice(testDevice, resetPair = false)
        advanceUntilIdle()
        Assert.assertEquals(
            listOf(
                DevicePairState.NotInitialized,
                DevicePairState.TimeoutConnecting(testDevice)
            ),
            pairStates
        )
        pairStatusJob.cancelAndJoin()
    }

    @Test
    fun `pair failed disconnect after initializing`() = runTest {
        underTest = PairDeviceViewModel(
            firstPairBleManagerFactory = firstPairBleManagerFactory,
            deviceColorSaver = mockk(relaxUnitFun = true),
            firstPairStorage = mockk(relaxUnitFun = true),
            dispatcher = StandardTestDispatcher(testScheduler)
        )
        DecomposeViewModelCoroutineScopeProvider.mockScope(this)
        val pairStates = mutableListOf<DevicePairState>()
        val pairStatusJob = underTest.getConnectionState().onEach {
            pairStates.add(it)
            println(it)
        }.launchIn(this)
        val testDevice = makeTestDevice()
        coEvery {
            bleManager.connectToDevice(any())
        } coAnswers { stateFlow.emit(ConnectionState.Initializing) }
        every {
            bleManager.bluetoothDevice
        } returns testDevice.device

        underTest.startConnectToDevice(testDevice, resetPair = false)
        advanceUntilIdle()
        stateFlow.emit(ConnectionState.Disconnected(ConnectionState.Disconnected.Reason.UNKNOWN))
        advanceUntilIdle()
        Assert.assertEquals(
            listOf(
                DevicePairState.NotInitialized,
                DevicePairState.WaitingForStart(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.Connecting(
                    address = "ADDRESS_TEST",
                    deviceName = "NAME_TEST"
                ),
                DevicePairState.TimeoutPairing(testDevice)
            ),
            pairStates
        )
        pairStatusJob.cancelAndJoin()
    }
}

private fun makeTestDevice(
    address: String = "ADDRESS_TEST",
    name: String = "NAME_TEST"
) = DiscoveredBluetoothDevice(
    device = mockk {
        every { getAddress() } returns address
        every { getName() } returns name
    },
    nameInternal = name,
    rssiInternal = 0,
    previousRssi = 0
)
