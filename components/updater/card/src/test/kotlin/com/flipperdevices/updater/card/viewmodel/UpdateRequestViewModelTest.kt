package com.flipperdevices.updater.card.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGattInformation
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.buildkonfig.BuildKonfig
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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class UpdateRequestViewModelTest {
    private lateinit var synchronizationState: MutableStateFlow<SynchronizationState>
    private lateinit var synchronizationApi: SynchronizationApi
    private lateinit var viewModel: UpdateRequestViewModel
    private lateinit var fFeatureProvider: FFeatureProvider
    private lateinit var fGattInfoFeatureApi: FGattInfoFeatureApi

    @Before
    fun setup() {
        fFeatureProvider = mockk(relaxUnitFun = true)
        fGattInfoFeatureApi = mockk(relaxUnitFun = true)
        mockkObject(FlipperDispatchers)
        every { FlipperDispatchers.workStealingDispatcher } returns Dispatchers.Main.immediate
        mockkStatic("com.flipperdevices.core.ktx.android.UriKtxKt")

        every { fFeatureProvider.get(FGattInfoFeatureApi::class) } returns flowOf(
            FFeatureStatus.Supported(
                fGattInfoFeatureApi
            )
        )

        synchronizationState = MutableStateFlow(
            SynchronizationState.NotStarted
        )
        synchronizationApi = mockk {
            every { getSynchronizationState() } returns synchronizationState
        }

        synchronizationState.update { SynchronizationState.NotStarted }
        every { fGattInfoFeatureApi.getGattInfoFlow() } answers {
            MutableStateFlow(FGattInformation(batteryLevel = 0.3f))
        }
        viewModel = UpdateRequestViewModel(
            fFeatureProvider = fFeatureProvider,
            synchronizationApi = synchronizationApi
        )
    }

    @Test
    fun `Battery state unknown`() = runTest {
        coEvery { fGattInfoFeatureApi.getGattInfoFlow() } answers {
            MutableStateFlow(FGattInformation())
        }
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Unknown)
    }

    @Test
    fun `Battery state more 10 percentage`() = runTest {
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Ready)
    }

    @Test
    fun `Battery state more 10 percentage but charge`() = runTest {
        every { fGattInfoFeatureApi.getGattInfoFlow() } returns MutableStateFlow(
            FGattInformation(
                batteryLevel = 0.4f,
                isCharging = true
            )
        )
        val state = viewModel.getBatteryState().first()
        Assert.assertTrue(state is BatteryState.Ready)
    }

    @Test
    fun `Open update request with sync complete`() = runTest {
        viewModel.getBatteryState()
            .filter { it !is BatteryState.Unknown }
            .first()
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
        viewModel.getBatteryState()
            .filter { it !is BatteryState.Unknown }
            .first()
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
        viewModel.getBatteryState()
            .filter { it !is BatteryState.Unknown }
            .first()
        viewModel.onUpdateRequest(UpdatePending.URI(uri, context, currentVersion))
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertEquals(
            UpdatePendingState.FileExtension,
            state
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
        viewModel.getBatteryState()
            .filter { it !is BatteryState.Unknown }
            .first()
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
        viewModel.getBatteryState()
            .filter { it !is BatteryState.Unknown }
            .first()
        viewModel.onUpdateRequest(UpdatePending.URI(uri, context, currentVersion))
        val state = viewModel.getUpdatePendingState().first()
        Assert.assertTrue(state is UpdatePendingState.Ready)
    }
}
