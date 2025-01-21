package com.flipperdevices.bridge.impl.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.core.buildkonfig.BuildKonfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanResult
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class FlipperScannerImplTest {
    private lateinit var scanner: BluetoothLeScannerCompat
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var context: Context
    private lateinit var flipperScanner: FlipperScanner

    @Before
    fun setUp() {
        ReflectionHelpers.setStaticField(
            Build.VERSION::class.java,
            "SDK_INT",
            Build.VERSION_CODES.S_V2
        )
        scanner = mockk()
        mockkStatic("com.flipperdevices.bridge.impl.scanner.FlowScanCallbackKt")
        bluetoothAdapter = mockk()
        context = mockk()
        every {
            context.checkPermission(
                Manifest.permission.BLUETOOTH_CONNECT,
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_GRANTED
        flipperScanner = FlipperScannerImpl(
            scanner,
            bluetoothAdapter,
            context
        )
    }

    @Test(expected = SecurityException::class)
    fun `request bluetooth permission`() {
        every {
            context.checkPermission(
                Manifest.permission.BLUETOOTH_CONNECT,
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_DENIED

        flipperScanner.findFlipperDevices()
    }

    @Test
    fun `not request bluetooth permission on old device`() {
        ReflectionHelpers.setStaticField(
            Build.VERSION::class.java,
            "SDK_INT",
            Build.VERSION_CODES.R
        )
        every {
            context.checkPermission(
                Manifest.permission.BLUETOOTH_CONNECT,
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_DENIED
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        flipperScanner.findFlipperDevices()
    }

    @Test
    fun `find correct single device`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns "Flipper Test"
            every { address } returns ""
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = flipperScanner.findFlipperDevices().first()

        val foundDevice = flipperDevices.firstOrNull()
        Assert.assertNotNull(foundDevice)
        Assert.assertEquals(bluetoothDevice, foundDevice!!.device)
    }

    @Test
    fun `find correct two device`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns "Flipper Test"
            every { address } returns ""
        }
        val secondBluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns "80:E1:26:AF:AF:26"
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)
        val sr2 = ScanResult(secondBluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr, sr2)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }
        val resultList = flipperDevices.last().toList()
        val foundDevice = resultList.getOrNull(0)
        val foundDevice2 = resultList.getOrNull(1)
        Assert.assertNotNull(foundDevice)
        Assert.assertNotNull(foundDevice2)
        Assert.assertEquals(bluetoothDevice, foundDevice!!.device)
        Assert.assertEquals(secondBluetoothDevice, foundDevice2!!.device)
        collectJob.cancelAndJoin()
    }

    @Test
    fun `filter device by mac`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns "80:E1:26:A1:4C:2D"
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = flipperScanner.findFlipperDevices().first()

        val foundDevice = flipperDevices.firstOrNull()
        Assert.assertNotNull(foundDevice)
        Assert.assertEquals(bluetoothDevice, foundDevice!!.device)
    }

    @Test
    fun `filter device by name`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns "Flipper Dumper"
            every { address } returns ""
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = flipperScanner.findFlipperDevices().first()

        val foundDevice = flipperDevices.firstOrNull()
        Assert.assertNotNull(foundDevice)
        Assert.assertEquals(bluetoothDevice, foundDevice!!.device)
    }

    @Test
    fun `block device with incorrect name`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns "Dupper"
            every { address } returns ""
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }

        Assert.assertTrue(flipperDevices.toList().isEmpty())

        collectJob.cancelAndJoin()
    }

    @Test
    fun `block device with empty name`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns ""
            every { address } returns ""
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }

        Assert.assertTrue(flipperDevices.toList().isEmpty())

        collectJob.cancelAndJoin()
    }

    @Test
    fun `block device with incorrect mac`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns "81:E1:26:A1:4C:2D"
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }

        Assert.assertTrue(flipperDevices.toList().isEmpty())

        collectJob.cancelAndJoin()
    }

    @Test
    fun `block device with empty mac`() = runTest {
        val bluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns ""
        }

        val sr = ScanResult(bluetoothDevice, 0, 0, 0, 0, 0, 0, 0, null, 0L)

        every { scanner.scanFlow(any(), any()) } returns flowOf(sr)
        every { bluetoothAdapter.bondedDevices } returns emptySet()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }

        Assert.assertTrue(flipperDevices.toList().isEmpty())

        collectJob.cancelAndJoin()
    }

    @Test
    fun `answer bounded device with correct mac`() = runTest {
        val alreadyConnectedBluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns "80:E1:26:A1:4C:2D"
        }

        every { bluetoothAdapter.bondedDevices } returns setOf(
            alreadyConnectedBluetoothDevice
        )

        every { scanner.scanFlow(any(), any()) } returns emptyFlow()

        val flipperDevices = flipperScanner.findFlipperDevices().first()

        val foundDevice = flipperDevices.firstOrNull()
        Assert.assertNotNull(foundDevice)
        Assert.assertEquals(alreadyConnectedBluetoothDevice, foundDevice!!.device)
    }

    @Test
    fun `answer bounded device with correct name`() = runTest {
        val alreadyConnectedBluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns "Flipper Test"
            every { address } returns ""
        }

        every { bluetoothAdapter.bondedDevices } returns setOf(
            alreadyConnectedBluetoothDevice
        )

        every { scanner.scanFlow(any(), any()) } returns emptyFlow()

        val flipperDevices = flipperScanner.findFlipperDevices().first()

        val foundDevice = flipperDevices.firstOrNull()
        Assert.assertNotNull(foundDevice)
        Assert.assertEquals(alreadyConnectedBluetoothDevice, foundDevice!!.device)
    }

    @Test
    fun `not answer bounded device without correct name or mac`() = runTest {
        val alreadyConnectedBluetoothDevice = mockk<BluetoothDevice> {
            every { name } returns null
            every { address } returns ""
        }

        every { bluetoothAdapter.bondedDevices } returns setOf(
            alreadyConnectedBluetoothDevice
        )

        every { scanner.scanFlow(any(), any()) } returns emptyFlow()

        val flipperDevices = mutableListOf<Iterable<DiscoveredBluetoothDevice>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            flipperScanner.findFlipperDevices().toList(flipperDevices)
        }

        Assert.assertTrue(flipperDevices.toList().isEmpty())

        collectJob.cancelAndJoin()
    }
}
