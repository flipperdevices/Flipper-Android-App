package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.ble.api.OverflowControlConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattService
import org.junit.Assert
import org.junit.Test
import java.util.UUID

private val config = FBleDeviceSerialConfig(
    serialServiceUuid = UUID.fromString("00000000-0000-0000-0000-000000000001"),
    rxServiceCharUuid = UUID.fromString("00000000-0000-0000-0000-000000000002"),
    txServiceCharUuid = UUID.fromString("00000000-0000-0000-0000-000000000003"),
    overflowControl = null,
    resetServiceUUID = UUID.fromString("00000000-0000-0000-0000-000000000004")
)

class SerialApiFactoryTest {
    @Test
    fun `return null if serial service is empty`() {
        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk(),
            throttlerApiFactory = mockk()
        )
        val result = underTest.build(
            config = config,
            scope = mockk(),
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns null
            }
        )

        Assert.assertNull(result)
    }

    @Test
    fun `return null if rx service is empty`() {
        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk(),
            throttlerApiFactory = mockk()
        )
        val rxCharacteristic: ClientBleGattCharacteristic? = null
        val txCharacteristic: ClientBleGattCharacteristic = mockk()
        val result = underTest.build(
            config = config,
            scope = mockk(),
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns mockk<ClientBleGattService> {
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                    } returns rxCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                    } returns txCharacteristic
                }
            }
        )

        Assert.assertNull(result)
    }

    @Test
    fun `return null if tx service is empty`() {
        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk(),
            throttlerApiFactory = mockk()
        )
        val rxCharacteristic: ClientBleGattCharacteristic = mockk()
        val txCharacteristic: ClientBleGattCharacteristic? = null
        val result = underTest.build(
            config = config,
            scope = mockk(),
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns mockk<ClientBleGattService> {
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                    } returns rxCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                    } returns txCharacteristic
                }
            }
        )

        Assert.assertNull(result)
    }

    @Test
    fun `return only serial api if overflow is null`() {
        val unsafeApiImpl: FSerialUnsafeApiImpl = mockk()
        val scope: CoroutineScope = mockk()
        val rxCharacteristic: ClientBleGattCharacteristic = mockk()
        val txCharacteristic: ClientBleGattCharacteristic = mockk()

        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk {
                every {
                    invoke(
                        rxCharacteristic = eq(rxCharacteristic),
                        txCharacteristic = eq(txCharacteristic),
                        scope = eq(scope)
                    )
                } returns unsafeApiImpl
            },
            throttlerApiFactory = mockk()
        )

        val result = underTest.build(
            config = config,
            scope = scope,
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns mockk<ClientBleGattService> {
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                    } returns rxCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                    } returns txCharacteristic
                }
            }
        )

        Assert.assertEquals(unsafeApiImpl, result)
    }

    @Test
    fun `return null if overflow char is null`() {
        val unsafeApiImpl: FSerialUnsafeApiImpl = mockk()
        val scope: CoroutineScope = mockk()
        val rxCharacteristic: ClientBleGattCharacteristic = mockk()
        val txCharacteristic: ClientBleGattCharacteristic = mockk()
        val overflowCharacteristic: ClientBleGattCharacteristic? = null

        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk {
                every {
                    invoke(
                        rxCharacteristic = eq(rxCharacteristic),
                        txCharacteristic = eq(txCharacteristic),
                        scope = eq(scope)
                    )
                } returns unsafeApiImpl
            },
            throttlerApiFactory = mockk()
        )

        val result = underTest.build(
            config = config.copy(
                overflowControl = OverflowControlConfig(UUID.fromString("00000000-0000-0000-0000-000000000005"))
            ),
            scope = scope,
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns mockk<ClientBleGattService> {
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                    } returns rxCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                    } returns txCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000005")))
                    } returns overflowCharacteristic
                }
            }
        )

        Assert.assertNull(result)
    }

    @Test
    fun `return overflow serial api`() {
        val unsafeApiImpl: FSerialUnsafeApiImpl = mockk()
        val throttlerApi: FSerialOverflowThrottler = mockk()
        val scope: CoroutineScope = mockk()
        val rxCharacteristic: ClientBleGattCharacteristic = mockk()
        val txCharacteristic: ClientBleGattCharacteristic = mockk()
        val overflowCharacteristic: ClientBleGattCharacteristic = mockk()

        val underTest = SerialApiFactory(
            unsafeApiImplFactory = mockk {
                every {
                    invoke(
                        rxCharacteristic = eq(rxCharacteristic),
                        txCharacteristic = eq(txCharacteristic),
                        scope = eq(scope)
                    )
                } returns unsafeApiImpl
            },
            throttlerApiFactory = mockk {
                every {
                    invoke(
                        serialApi = eq(unsafeApiImpl),
                        scope = eq(scope),
                        overflowCharacteristic = eq(overflowCharacteristic)
                    )
                } returns throttlerApi
            }
        )

        val result = underTest.build(
            config = config.copy(
                overflowControl = OverflowControlConfig(UUID.fromString("00000000-0000-0000-0000-000000000005"))
            ),
            scope = scope,
            services = mockk {
                every {
                    findService(eq(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                } returns mockk<ClientBleGattService> {
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                    } returns rxCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000003")))
                    } returns txCharacteristic
                    every {
                        findCharacteristic(eq(UUID.fromString("00000000-0000-0000-0000-000000000005")))
                    } returns overflowCharacteristic
                }
            }
        )

        Assert.assertEquals(throttlerApi, result)
    }
}
