package com.flipperdevices.updater.card.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(AndroidJUnit4::class)
class UpdateRequestViewModelTest {
    private val serviceProvider: FlipperServiceProvider = mockk(relaxUnitFun = true)
    private val serviceApi: FlipperServiceApi = mockk()
    private val updaterUIApi: UpdaterUIApi = mockk(relaxUnitFun = true)
    private val viewModel = UpdateRequestViewModel(
        serviceProvider = serviceProvider,
        updaterUIApi = updaterUIApi,
        deeplinkParser = mockk()
    )

    @Test
    fun `Battery state ready, not charging`() = runTest {
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation(batteryLevel = 0.3f, isCharging = false))
        }
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().filter { it != BatteryState.Unknown }.first()
        assert(state is BatteryState.Ready)
        val localState = state as BatteryState.Ready
        Assert.assertEquals(localState.isCharging, false)
        Assert.assertEquals(localState.batteryLevel, 0.3f)
    }

    @Test
    fun `Battery state ready, charging`() = runTest {
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation(batteryLevel = 0.3f, isCharging = true))
        }
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().filter { it != BatteryState.Unknown }.first()
        assert(state is BatteryState.Ready)
        val localState = state as BatteryState.Ready
        Assert.assertEquals(localState.isCharging, true)
        Assert.assertEquals(localState.batteryLevel, 0.3f)
    }

    @Test
    fun `Battery state unknown`() = runTest {
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation())
        }
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().first()
        assert(state is BatteryState.Unknown)
    }

    @Test
    fun `verify call open updater screen`() {
        val fmVersion = FirmwareVersion(channel = FirmwareChannel.DEV, version = "")
        val file = DistributionFile(url = "", sha256 = "")
        viewModel.openUpdate(
            UpdateCardState.UpdateAvailable(
                update = UpdateRequest(
                    updateFrom = fmVersion,
                    updateTo = fmVersion,
                    content = OfficialFirmware(DistributionFile(url = "", sha256 = "")),
                    changelog = null
                ),
                isOtherChannel = true
            )
        )
        verify {
            updaterUIApi.openUpdateScreen(
                withArg {
                    Assert.assertFalse(it)
                },
                withArg {
                    Assert.assertEquals(it.updateFrom, fmVersion)
                    Assert.assertEquals(it.updateTo, fmVersion)
                    Assert.assertEquals(it.changelog, null)
                    Assert.assertEquals(it.content, file)
                }
            )
        }
    }
}
