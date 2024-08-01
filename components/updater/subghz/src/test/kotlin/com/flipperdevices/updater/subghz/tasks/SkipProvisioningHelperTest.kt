package com.flipperdevices.updater.subghz.tasks

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getResponse
import com.flipperdevices.updater.subghz.helpers.SkipProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.SkipProvisioningHelperImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SkipProvisioningHelperTest {
    private lateinit var settings: DataStore<Settings>
    private lateinit var underTest: SkipProvisioningHelper

    @Before
    fun setUp() {
        settings = mockk()
        underTest = SkipProvisioningHelperImpl(settings)

        mockkObject(TimeHelper)
        every { TimeHelper.getNanoTime() } returns 0L
    }

    @Test
    fun `not skip provisioning if settings disable`() = runTest {
        every { settings.data } returns flowOf(
            Settings(
                ignore_subghz_provisioning_on_zero_region = false
            )
        )

        val shouldProvide = underTest.shouldSkipProvisioning(mockk())

        Assert.assertFalse(shouldProvide)
    }

    @Test
    fun `not skip provisioning if version null`() = runTest {
        every { settings.data } returns flowOf(
            Settings(
                ignore_subghz_provisioning_on_zero_region = true
            )
        )

        val versionApi = mockk<FlipperVersionApi> {
            coEvery {
                isSupported(eq(Constants.API_SUPPORTED_GET_REQUEST), any())
            } returns false
        }

        val shouldProvide = underTest.shouldSkipProvisioning(
            mockk {
                every { flipperVersionApi } returns versionApi
            }
        )

        Assert.assertFalse(shouldProvide)
    }

    @Test
    fun `not skip provisioning if version deprecated`() = runTest {
        every { settings.data } returns flowOf(
            Settings(
                ignore_subghz_provisioning_on_zero_region = true
            )
        )

        val versionApi = mockk<FlipperVersionApi> {
            coEvery {
                isSupported(eq(Constants.API_SUPPORTED_GET_REQUEST), any())
            } returns false
        }

        val shouldProvide = underTest.shouldSkipProvisioning(
            mockk {
                every { flipperVersionApi } returns versionApi
            }
        )

        Assert.assertFalse(shouldProvide)
    }

    @Test
    fun `not skip provisioning if hardware region null`() = runTest {
        every { settings.data } returns flowOf(

            Settings(
                ignore_subghz_provisioning_on_zero_region = true
            )
        )

        val versionApi = mockk<FlipperVersionApi> {
            coEvery {
                isSupported(eq(Constants.API_SUPPORTED_GET_REQUEST), any())
            } returns true
        }
        val mockRequestApi = mockk<FlipperRequestApi> {
            coEvery { request(any(), any()) } returns main {}
        }

        val shouldProvide = underTest.shouldSkipProvisioning(
            mockk {
                every { flipperVersionApi } returns versionApi
                every { requestApi } returns mockRequestApi
            }
        )

        Assert.assertFalse(shouldProvide)
    }

    @Test
    fun `not skip provisioning if hardware region not zero`() = runTest {
        every { settings.data } returns flowOf(
            Settings(
                ignore_subghz_provisioning_on_zero_region = true
            )
        )

        val versionApi = mockk<FlipperVersionApi> {
            coEvery {
                isSupported(eq(Constants.API_SUPPORTED_GET_REQUEST), any())
            } returns true
        }
        val mockRequestApi = mockk<FlipperRequestApi> {
            coEvery { request(any(), any()) } returns main {
                propertyGetResponse = getResponse {
                    value = "1"
                }
            }
        }

        val shouldProvide = underTest.shouldSkipProvisioning(
            mockk {
                every { flipperVersionApi } returns versionApi
                every { requestApi } returns mockRequestApi
            }
        )

        Assert.assertFalse(shouldProvide)
    }

    @Test
    fun `skip provisioning`() = runTest {
        every { settings.data } returns flowOf(
            Settings(
                ignore_subghz_provisioning_on_zero_region = true
            )
        )

        val versionApi = mockk<FlipperVersionApi> {
            coEvery {
                isSupported(eq(Constants.API_SUPPORTED_GET_REQUEST), any())
            } returns true
        }
        val mockRequestApi = mockk<FlipperRequestApi> {
            coEvery { request(any(), any()) } returns main {
                propertyGetResponse = getResponse {
                    value = "0"
                }
            }
        }

        val shouldProvide = underTest.shouldSkipProvisioning(
            mockk {
                every { flipperVersionApi } returns versionApi
                every { requestApi } returns mockRequestApi
            }
        )

        Assert.assertTrue(shouldProvide)
    }
}
