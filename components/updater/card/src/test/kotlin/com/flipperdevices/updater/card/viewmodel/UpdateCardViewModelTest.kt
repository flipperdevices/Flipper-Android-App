package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.preference.pb.SelectedChannel
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.settings
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.SubGhzProvisioningHelperApi
import com.flipperdevices.updater.card.utils.FileExistHelper
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.VersionFiles
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.EnumMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(AndroidJUnit4::class)
class UpdateCardViewModelTest {

    private val downloaderApi: DownloaderApi = mockk()
    private val flipperVersionProviderApi: FlipperVersionProviderApi = mockk()
    private val serviceProvider: FlipperServiceProvider = mockk(relaxUnitFun = true)
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val serviceApi: FlipperServiceApi = mockk()
    private val fileExistHelper: FileExistHelper = mockk()
    private val subGhzProvisioningHelperApi: SubGhzProvisioningHelperApi = mockk()
    private val updateCardViewModel: UpdateCardViewModel = UpdateCardViewModel(
        downloaderApi = downloaderApi,
        flipperVersionProviderApi = flipperVersionProviderApi,
        serviceProvider = serviceProvider,
        dataStoreSettings = dataStoreSettings,
        fileExistHelper = fileExistHelper,
        subGhzProvisioningHelperApi = subGhzProvisioningHelperApi
    )

    @Before
    fun setup() {
        every {
            flipperVersionProviderApi.getCurrentFlipperVersion(
                updateCardViewModel.viewModelScope,
                serviceApi
            )
        } returns MutableStateFlow(FirmwareVersion(FirmwareChannel.DEV, version = "0.54.3"))
        coEvery { downloaderApi.getLatestVersion() } answers {
            val versionMap: EnumMap<FirmwareChannel, VersionFiles> =
                EnumMap(FirmwareChannel::class.java)
            versionMap[FirmwareChannel.DEV] = VersionFiles(
                version = FirmwareVersion(FirmwareChannel.DEV, "0.54.3"),
                updaterFile = DistributionFile("", ""),
                changelog = ""
            )
            versionMap
        }
        coEvery { subGhzProvisioningHelperApi.getRegion() } returns "UA"
        every { serviceApi.flipperRpcInformationApi } returns mockk()
        every {
            serviceApi.flipperRpcInformationApi.getRpcInformationFlow()
        } returns MutableStateFlow(
            FlipperRpcInformation(
                internalStorageStats = StorageStats.Loaded(0, 0),
                externalStorageStats = StorageStats.Loaded(0, 0)
            )
        )
        every { serviceApi.requestApi } returns mockk()
        every { dataStoreSettings.data } returns flowOf(
            settings {
                selectedChannel = SelectedChannel.DEV
                alwaysUpdate = false
                region = "UA"
            }
        )
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.MANIFEST_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(true)
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.REGION_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(true)
    }

    @Test
    fun `No flags for new update`() = runTest {
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.NoUpdate)
    }

    @Test
    fun `Not exist manifest on flipper`() = runTest {
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.MANIFEST_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(false)
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.UpdateAvailable)
    }

    @Test
    fun `Not exist provisioning file subghz on flipper`() = runTest {
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.REGION_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(false)
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.UpdateAvailable)
    }

    @Test
    fun `Always update in settings`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                selectedChannel = SelectedChannel.DEV
                alwaysUpdate = true
            }
        )
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.UpdateAvailable)
    }

    @Test
    fun `The region now and the previous one are different`() = runTest {
        coEvery { subGhzProvisioningHelperApi.getRegion() } returns "USA"
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.UpdateAvailable)
    }

    @Test
    fun `The region now and the previous one are same`() = runTest {
        // UA now, previous UA too
        updateCardViewModel.onServiceApiReady(serviceApi)
        val state = updateCardViewModel
            .getUpdateCardState()
            .filter { it != UpdateCardState.InProgress }
            .first()
        Assert.assertTrue(state is UpdateCardState.NoUpdate)
    }
}
