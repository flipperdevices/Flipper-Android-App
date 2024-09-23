package com.flipperdevices.updater.card.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.filename
import com.flipperdevices.core.ktx.android.length
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.card.model.SyncingState
import com.flipperdevices.updater.card.model.UpdatePending
import com.flipperdevices.updater.card.model.UpdatePendingState
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private val requestServer = UpdatePending.Request(
    updateRequest = UpdateRequest(
        updateFrom = FirmwareVersion(channel = FirmwareChannel.DEV, version = ""),
        updateTo = FirmwareVersion(channel = FirmwareChannel.DEV, version = ""),
        content = OfficialFirmware(DistributionFile(url = "", sha256 = "")),
        changelog = null
    )
)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class UpdateRequestViewModelTest {
    private lateinit var serviceProvider: FlipperServiceProvider
    private lateinit var serviceApi: FlipperServiceApi
    private lateinit var synchronizationState: MutableStateFlow<SynchronizationState>
    private lateinit var synchronizationApi: SynchronizationApi
    private lateinit var viewModel: UpdateRequestViewModel

    @Before
    fun setup() {
        mockkObject(FlipperDispatchers)
        every { FlipperDispatchers.workStealingDispatcher } returns Dispatchers.Main.immediate
        mockkStatic("com.flipperdevices.core.ktx.android.UriKtxKt")

        serviceProvider = mockk(relaxUnitFun = true)
        serviceApi = mockk(relaxUnitFun = true)
        synchronizationState = MutableStateFlow(
            SynchronizationState.NotStarted
        )
        synchronizationApi = mockk {
            every { getSynchronizationState() } returns synchronizationState
        }

        synchronizationState.update { SynchronizationState.NotStarted }
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation(batteryLevel = 0.3f))
        }
        viewModel = UpdateRequestViewModel(
            serviceProvider = serviceProvider,
            synchronizationApi = synchronizationApi
        )
        viewModel.onServiceApiReady(serviceApi)
    }

    @Test
    fun `Battery state unknown`() = runTest {
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation())
        }
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Unknown)
    }

    @Test
    fun `Battery state more 10 percentage`() = runTest {
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Ready)
    }

    @Test
    fun `Battery state more 10 percentage but charge`() = runTest {
        every { serviceApi.flipperInformationApi.getInformationFlow() } answers {
            MutableStateFlow(FlipperGATTInformation(batteryLevel = 0.09f, isCharging = true))
        }
        viewModel.onServiceApiReady(serviceApi)
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Ready)
    }

    @Test
    fun `Open update request with sync complete`() = runTest {
        viewModel.onUpdateRequest(requestServer)
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            UpdatePendingState.Ready(
                requestServer.updateRequest,
                SyncingState.COMPLETE
            )
        )
    }

    @Test
    fun `Open update request with sync`() = runTest {
        synchronizationState.emit(SynchronizationState.InProgress.Default(0f))
        viewModel.onUpdateRequest(requestServer)
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            UpdatePendingState.Ready(
                requestServer.updateRequest,
                SyncingState.IN_PROGRESS
            )
        )
    }

    @Test
    fun `Stop sync and start update`() = runTest {
        viewModel.stopSyncAndStartUpdate(requestServer.updateRequest)
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            UpdatePendingState.Ready(
                requestServer.updateRequest,
                SyncingState.STOP
            )
        )
    }

    @Test
    fun `Reset state`() = runTest {
        viewModel.resetState()
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            null
        )
    }

    @Test
    fun `Not update because file not tgz`() = runTest {
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val uri = Uri.EMPTY
        val currentVersion = FirmwareVersion(channel = FirmwareChannel.CUSTOM, version = "")

        every { context.contentResolver } returns contentResolver
        every { uri.length(context.contentResolver) } answers { 0L }
        every { uri.filename(context.contentResolver) } answers { "file.zip" }

        viewModel.onUpdateRequest(UpdatePending.URI(uri, context, currentVersion))
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            UpdatePendingState.FileExtension
        )
    }

    @Test
    fun `Not update because file bigger`() = runTest {
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val uri = Uri.EMPTY
        val currentVersion = FirmwareVersion(channel = FirmwareChannel.CUSTOM, version = "")

        every { context.contentResolver } returns contentResolver
        every { uri.length(contentResolver) } answers { 1024 * 1024 * 1024L * 10 + 1 }
        every { uri.filename(contentResolver) } answers { "file.tgz" }

        viewModel.onUpdateRequest(UpdatePending.URI(uri, context, currentVersion))
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            state,
            UpdatePendingState.FileBig
        )
    }

    @Test
    fun `Ready update from internal`() = runTest {
        val context = mockk<Context>()
        val contentResolver = mockk<ContentResolver>()
        val uri = Uri.EMPTY
        val currentVersion = FirmwareVersion(channel = FirmwareChannel.CUSTOM, version = "")

        every { context.contentResolver } returns contentResolver
        every { uri.length(context.contentResolver) } answers { 1L }
        every { uri.filename(context.contentResolver) } answers { "file.tgz" }

        viewModel.onUpdateRequest(UpdatePending.URI(uri, context, currentVersion))
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertTrue(state is UpdatePendingState.Ready)
    }
}
