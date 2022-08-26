package com.flipperdevices.updater.card.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.settings
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.card.utils.FileExistHelper
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(AndroidJUnit4::class)
class UpdateCardViewModelTest {

    private val downloaderApi: DownloaderApi = mockk()
    private val flipperVersionProviderApi: FlipperVersionProviderApi = mockk()
    private val serviceProvider: FlipperServiceProvider = mockk()
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val serviceApi: FlipperServiceApi = mockk()
    private val fileExistHelper: FileExistHelper = FileExistHelper()
    private val updateCardViewModel: UpdateCardViewModel = UpdateCardViewModel(
        downloaderApi = downloaderApi,
        flipperVersionProviderApi = flipperVersionProviderApi,
        serviceProvider = serviceProvider,
        dataStoreSettings = dataStoreSettings,
        fileExistHelper = fileExistHelper
    )

    @Before
    fun setup() {
        every { serviceProvider.provideServiceApi(mockk(), mockk()) } just Runs
        every { serviceApi.flipperRpcInformationApi } returns mockk()
        every {
            serviceApi.flipperRpcInformationApi.getRpcInformationFlow()
        } returns MutableStateFlow(FlipperRpcInformation())
        every { serviceApi.requestApi } returns mockk()
    }

    @Test
    fun `Not exist manifest on flipper`() = runTest {
        every { dataStoreSettings.data } returns flowOf(settings {})
        every {
            flipperVersionProviderApi.getCurrentFlipperVersion(
                updateCardViewModel.viewModelScope,
                serviceApi
            )
        } returns MutableStateFlow(FirmwareVersion(FirmwareChannel.DEV, version = "0.54.3"))
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.REGION_FILE,
                requestApi = mockk()
            )
        } returns flowOf(false)
        updateCardViewModel.onServiceApiReady(serviceApi)
    }
}
