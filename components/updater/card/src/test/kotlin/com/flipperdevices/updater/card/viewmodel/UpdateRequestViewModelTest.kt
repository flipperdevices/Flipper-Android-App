package com.flipperdevices.updater.card.viewmodel

import android.content.Context
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.getClearName
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.InternalStorageFirmware
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateRequest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
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
    private val deeplinkParser: DeepLinkParser = mockk()
    private val viewModel = UpdateRequestViewModel(
        serviceProvider = serviceProvider,
        updaterUIApi = updaterUIApi,
        deeplinkParser = deeplinkParser
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
    fun `verify call open update from official source`() {
        val fmVersion = FirmwareVersion(channel = FirmwareChannel.DEV, version = "")
        val content = OfficialFirmware(DistributionFile(url = "", sha256 = ""))
        viewModel.openUpdate(
            UpdatePending.Request(
                updateRequest = UpdateRequest(
                    updateFrom = fmVersion,
                    updateTo = fmVersion,
                    content = content,
                    changelog = null
                )
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
                    Assert.assertEquals(it.content, content)
                }
            )
        }
    }

    @Test
    fun `verify call open update from internal storage`() {
        val currentFW = FirmwareVersion(channel = FirmwareChannel.DEV, version = "")
        val context: Context = mockk()
        val path = "/test/text.txt"
        val file = File(path)
        val uri = Uri.EMPTY
        coEvery { deeplinkParser.fromUri(context, uri) } coAnswers {
            Deeplink(path = null, content = DeeplinkContent.InternalStorageFile(path))
        }
        viewModel.openUpdate(
            UpdatePending.URI(
                uri = uri,
                context = context,
                currentVersion = currentFW
            )
        )
        verify {
            updaterUIApi.openUpdateScreen(
                withArg {
                    Assert.assertFalse(it)
                },
                withArg {
                    Assert.assertEquals(it.updateFrom, currentFW)
                    Assert.assertEquals(
                        it.updateTo,
                        FirmwareVersion(
                            channel = FirmwareChannel.CUSTOM,
                            version = file.getClearName()
                        )
                    )
                    Assert.assertEquals(it.changelog, null)
                    assert(it.content is InternalStorageFirmware)
                }
            )
        }
    }
}
