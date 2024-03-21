package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices
import org.junit.Before
import org.junit.Test
import java.util.UUID

class FSerialDeviceApiWrapperTest {
    private lateinit var config: FBleDeviceSerialConfig
    private lateinit var serviceFlow: MutableStateFlow<ClientBleGattServices?>
    private lateinit var serialApiFactory: SerialApiFactory

    @Before
    fun setUp() {
        config = FBleDeviceSerialConfig(
            serialServiceUuid = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            rxServiceCharUuid = UUID.fromString("00000000-0000-0000-0000-000000000002"),
            txServiceCharUuid = UUID.fromString("00000000-0000-0000-0000-000000000003"),
            overflowControl = null,
            resetServiceUUID = UUID.fromString("00000000-0000-0000-0000-000000000004")
        )
        serviceFlow = MutableStateFlow(null)
        serialApiFactory = mockk {
            every { build(any(), any(), any()) } returns null
        }
    }

    @Test
    fun `send byte in serial device api`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialDeviceApiWrapper(
            scope = childScope,
            config = config,
            serviceFlow = serviceFlow,
            serialApiFactory = serialApiFactory
        )
        val services: ClientBleGattServices = mockk()
        val serialApi: FSerialDeviceApi = mockk(relaxUnitFun = true)

        every {
            serialApiFactory.build(
                config = eq(config),
                services = eq(services),
                scope = any()
            )
        } returns serialApi

        serviceFlow.emit(services)

        childScope.advanceUntilIdle()

        childScope.launch {
            underTest.sendBytes("TEST".toByteArray())
        }

        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq("TEST".toByteArray())) }

        childScope.cancel()
    }

    @Test
    fun `wait if services not provided`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialDeviceApiWrapper(
            scope = childScope,
            config = config,
            serviceFlow = serviceFlow,
            serialApiFactory = serialApiFactory
        )
        val services: ClientBleGattServices = mockk()
        val serialApi: FSerialDeviceApi = mockk(relaxUnitFun = true)

        every {
            serialApiFactory.build(
                config = eq(config),
                services = eq(services),
                scope = any()
            )
        } returns serialApi

        childScope.advanceUntilIdle()

        childScope.launch {
            underTest.sendBytes("TEST".toByteArray())
        }

        childScope.advanceUntilIdle()

        coVerify(inverse = true) { serialApi.sendBytes(eq("TEST".toByteArray())) }

        childScope.cancel()
    }

    @Test
    fun `wait if serialApi is null`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialDeviceApiWrapper(
            scope = childScope,
            config = config,
            serviceFlow = serviceFlow,
            serialApiFactory = serialApiFactory
        )
        val services: ClientBleGattServices = mockk()
        val serialApi: FSerialDeviceApi = mockk(relaxUnitFun = true)

        every {
            serialApiFactory.build(
                config = eq(config),
                services = eq(services),
                scope = any()
            )
        } returns null

        serviceFlow.emit(services)

        childScope.advanceUntilIdle()

        childScope.launch {
            underTest.sendBytes("TEST".toByteArray())
        }

        childScope.advanceUntilIdle()

        coVerify(inverse = true) { serialApi.sendBytes(eq("TEST".toByteArray())) }

        childScope.cancel()
    }

    @Test
    fun `wait until services provided`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialDeviceApiWrapper(
            scope = childScope,
            config = config,
            serviceFlow = serviceFlow,
            serialApiFactory = serialApiFactory
        )
        val services: ClientBleGattServices = mockk()
        val serialApi: FSerialDeviceApi = mockk(relaxUnitFun = true)

        every {
            serialApiFactory.build(
                config = eq(config),
                services = eq(services),
                scope = any()
            )
        } returns serialApi

        childScope.advanceUntilIdle()

        childScope.launch {
            underTest.sendBytes("TEST".toByteArray())
        }

        childScope.advanceUntilIdle()

        coVerify(inverse = true) { serialApi.sendBytes(eq("TEST".toByteArray())) }

        serviceFlow.emit(services)

        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq("TEST".toByteArray())) }

        childScope.cancel()
    }
}
