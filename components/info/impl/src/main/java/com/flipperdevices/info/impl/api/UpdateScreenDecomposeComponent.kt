package com.flipperdevices.info.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.info.impl.compose.screens.ComposableDeviceInfoScreen
import com.flipperdevices.info.impl.model.DeviceScreenNavigationConfig
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.impl.viewmodel.FlipperColorViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.BasicInfoViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.api.UpdaterFeatureEntry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

@Suppress("LongParameterList")
class UpdateScreenDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val deeplink: Deeplink?,
    @Assisted private val navigator: StackNavigator<DeviceScreenNavigationConfig>,
    private val updaterCardApi: UpdaterCardApi,
    private val updaterFeatureEntry: UpdaterFeatureEntry,
    private val deviceStatusViewModelProvider: Provider<DeviceStatusViewModel>,
    private val connectViewModelProvider: Provider<ConnectViewModel>,
    private val flipperColorProvider: Provider<FlipperColorViewModel>,
    private val firmwareUpdateViewModelProvider: Provider<FirmwareUpdateViewModel>,
    private val alarmViewModelProvider: Provider<AlarmViewModel>,
    private val basicInfoViewModelProvider: Provider<BasicInfoViewModel>
) : DecomposeComponent, ComponentContext by componentContext {

    @Suppress("NonSkippableComposable")
    @Composable
    override fun Render() {
        val globalNavController = LocalGlobalNavigationNavStack.current
        val deviceStatusViewModel = viewModelWithFactory(key = null) {
            deviceStatusViewModelProvider.get()
        }
        val deviceStatus by deviceStatusViewModel.getState().collectAsState()
        val updateState by deviceStatusViewModel.getUpdateState().collectAsState()
        val connectViewModel = viewModelWithFactory(key = null) {
            connectViewModelProvider.get()
        }
        val flipperColorViewModel = viewModelWithFactory(key = null) {
            flipperColorProvider.get()
        }
        val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()
        val firmwareUpdateViewModel = viewModelWithFactory(key = null) {
            firmwareUpdateViewModelProvider.get()
        }
        val supportState by firmwareUpdateViewModel.getState().collectAsState()
        val alarmViewModel = viewModelWithFactory(key = null) {
            alarmViewModelProvider.get()
        }
        val basicInfoViewModel = viewModelWithFactory(key = null) {
            basicInfoViewModelProvider.get()
        }
        val basicInfo by basicInfoViewModel.getDeviceInfo().collectAsState()

        ComposableDeviceInfoScreen(
            updaterCardApi = updaterCardApi,
            onOpenFullDeviceInfo = {
                navigator.push(DeviceScreenNavigationConfig.FullInfo)
            },
            onOpenOptions = {
                navigator.push(DeviceScreenNavigationConfig.Options)
            },
            onStartUpdateRequest = {
                globalNavController.navigate(updaterFeatureEntry.getUpdaterScreen(it))
            },
            deeplink = deeplink,
            deviceStatus = deviceStatus,
            connectViewModel = connectViewModel,
            hardwareColor = flipperColor,
            supportedState = supportState,
            updateState = updateState,
            alarmOnFlipper = alarmViewModel::alarmOnFlipper,
            deviceInfo = basicInfo
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink?,
            navigator: StackNavigator<DeviceScreenNavigationConfig>
        ): UpdateScreenDecomposeComponent
    }
}
